package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;
import ru.mindils.jb.sync.mapper.EmployerMapper;
import ru.mindils.jb.sync.mapper.VacancyMapper;
import ru.mindils.jb.sync.util.EmployerUtil;

@Service
@RequiredArgsConstructor
public class SyncVacancyService {

    private static final String DEFAULT_FILTER_PERIOD = "30";
    private static final String DEFAULT_ITEMS_PER_PAGE = "100";
    private final VacancyClientService vacancyApiClientService;
    private final VacancyFilterService vacancyFilterService;
    private final VacancyMapper vacancyMapper;
    private final EmployerMapper employerMapper;
    private final EntityManager entityManager;

    @SneakyThrows
    public void syncEmployerDetailsAll() {

        // вакансий может быть много, поэтому заполняем пачками
        while (true) {
            List<Employer> employers =
                    entityManager
                            .createQuery(
                                    "select e from Employer e where e.detailed = false",
                                    Employer.class)
                            .setFirstResult(0)
                            .setMaxResults(10)
                            .getResultList();

            if (employers.isEmpty()) {
                break;
            }

            employers.forEach(
                    employer -> {
                        syncEmployerById(employer.getId());
                    });

            // спим, чтобы не нагружать hh
            // TODO: попробовать переделать на scheduler когда добавим spring
            //  employers.forEach(employer -> {
            //    scheduler.schedule(() -> syncEmployerById(employer), 1, TimeUnit.SECONDS);
            //  });
            Thread.sleep(1000);
        }
    }

    @SneakyThrows
    public void syncVacancyDetailsAll() {

        while (true) {
            List<Vacancy> vacancies =
                    entityManager
                            .createQuery(
                                    "select e from Vacancy e where e.detailed = false",
                                    Vacancy.class)
                            .setFirstResult(0)
                            .setMaxResults(100)
                            .getResultList();

            if (vacancies.isEmpty()) {
                break;
            }

            vacancies.forEach(
                    vacancy -> {
                        syncVacancyById(vacancy.getId());
                    });

            // спим, чтобы не нагружать hh
            // TODO: попробовать переделать на scheduler когда добавим spring
            //  vacancies.forEach(vacancy -> {
            //    scheduler.schedule(() -> syncVacancyById(vacancy), 1, TimeUnit.SECONDS);
            //  });
            Thread.sleep(1000);
        }
    }

    public void syncVacancyByDefaultFilterAll() {
        syncVacancyByDefaultFilterAll(DEFAULT_FILTER_PERIOD);
    }

    @SneakyThrows
    public void syncVacancyByDefaultFilterAll(String period) {
        List<Map<String, String>> defaultFilter = vacancyFilterService.getDefaultFilter();

        int page = 0;

        VacancyListResponseDto vacancyListResponseDto;

        do {
            List<Map<String, String>> updatedFilter = new ArrayList<>(defaultFilter);

            updatedFilter.add(Map.of("period", period));
            updatedFilter.add(Map.of("per_page", DEFAULT_ITEMS_PER_PAGE));
            updatedFilter.add(Map.of("page", String.valueOf(page)));

            // задержка, чтобы не грузить hh.ru
            Thread.sleep(1000);

            vacancyListResponseDto = syncVacancyFilter(updatedFilter);
            page = vacancyListResponseDto.getPage() + 1;

        } while (vacancyListResponseDto.getPage() < vacancyListResponseDto.getPages() - 1);
    }

    /**
     * TODO: переписать, тут надо чтобы мы получали вакансии где нет рейтинга и обновляли его в
     * данном случае может быть не создана запись в vacancy_info и мы не обновим рейтинг
     */
    public void syncVacancyAiRatingsAll() {
        while (true) {
            List<VacancyInfo> vacancies =
                    entityManager
                            .createQuery(
                                    "select e from VacancyInfo e join e.vacancy where"
                                            + " e.aiApproved is null",
                                    VacancyInfo.class)
                            .setFirstResult(0)
                            .setMaxResults(100)
                            .getResultList();

            if (vacancies.isEmpty()) {
                break;
            }

            // тут можно без сна, т.к. наш сервер )
            vacancies.forEach(this::syncVacancyAiRating);
        }
    }

    public void syncEmployerById(String id) {
        DetailedEmployerDto objects = vacancyApiClientService.loadEmployerById(id);

        entityManager.getTransaction().begin();

        if (objects.getId() == null) {
            // если не нашли работодателя, то создаем пустого
            Employer employerEmpty = EmployerUtil.getEmployerEmpty(id);
            entityManager.merge(employerEmpty);
        } else {
            Employer vacancy = entityManager.find(Employer.class, id);
            Employer employerMap = entityManager.merge(employerMapper.map(objects, vacancy));
            entityManager.merge(employerMap);
        }

        entityManager.getTransaction().commit();
    }

    public void syncVacancyById(String id) {
        DetailedVacancyDto vacancyDto = vacancyApiClientService.loadVacancyById(id);

        entityManager.getTransaction().begin();
        Vacancy vacancy = entityManager.find(Vacancy.class, id);

        // Workaround:
        // если при загрузке деталей вакансии получаем 404 от hh помечаем, что делали загружены
        if (vacancyDto.getId() == null) {
            vacancy.setDetailed(true);
            entityManager.merge(vacancy);
        } else {
            Vacancy mapVacancy = entityManager.merge(vacancyMapper.map(vacancyDto, vacancy));

            // TODO: тут если работодатель уже загружен с деталями то не нужно его обновлять
            //  затирается поле detailed
            entityManager.merge(mapVacancy.getEmployer());
            entityManager.merge(mapVacancy);
        }
        entityManager.getTransaction().commit();
    }

    public void syncVacancyAiRating(VacancyInfo vacancyInfo) {
        String sb =
                vacancyInfo.getVacancy().getName() + " " + vacancyInfo.getVacancy().getKeySkills();
        String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

        entityManager.getTransaction().begin();
        vacancyInfo.setAiApproved(new BigDecimal(ratingAi));

        entityManager.merge(vacancyInfo);

        entityManager.getTransaction().commit();
    }

    private VacancyListResponseDto syncVacancyFilter(List<Map<String, String>> defaultFilter) {
        VacancyListResponseDto vacancyListResponseDto =
                vacancyApiClientService.loadVacancies(defaultFilter);

        List<String> vacancyIds =
                vacancyListResponseDto.getItems().stream().map(BriefVacancyDto::getId).toList();

        entityManager.getTransaction().begin();

        // Получаем все которые ранее были загружены, чтобы обновить
        Map<String, Vacancy> vacancyMaps =
                entityManager
                        .createQuery("select e from Vacancy e where e.id in :ids", Vacancy.class)
                        .setParameter("ids", vacancyIds)
                        .getResultList()
                        .stream()
                        .collect(Collectors.toMap(Vacancy::getId, Function.identity()));

        vacancyListResponseDto
                .getItems()
                .forEach(
                        vacancyDto -> {
                            Vacancy vacancy;
                            if (vacancyMaps.containsKey(vacancyDto.getId())) {
                                vacancy =
                                        vacancyMapper.map(
                                                vacancyDto, vacancyMaps.get(vacancyDto.getId()));
                            } else {
                                vacancy = vacancyMapper.map(vacancyDto);
                            }

                            // TODO: тут если работодатель уже загружен с деталями то не нужно
                            // его обновлять
                            entityManager.merge(vacancy.getEmployer());
                            entityManager.merge(vacancy);
                        });

        entityManager.getTransaction().commit();
        return vacancyListResponseDto;
    }
}
