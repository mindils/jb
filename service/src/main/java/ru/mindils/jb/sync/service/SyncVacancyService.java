package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;
import ru.mindils.jb.sync.mapper.EmployerMapper;
import ru.mindils.jb.sync.mapper.VacancyMapper;
import ru.mindils.jb.sync.util.EmployerUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncVacancyService {

    private static final String DEFAULT_FILTER_PERIOD = "30";
    private static final String DEFAULT_ITEMS_PER_PAGE = "100";
    private final VacancyClientService vacancyApiClientService;
    private final VacancyFilterService vacancyFilterService;
    private final VacancyMapper vacancyMapper;
    private final EmployerMapper employerMapper;
    private final EntityManager entityManager;

    public boolean syncEmployerDetailsBatch() {
        List<Employer> employers =
                entityManager
                        .createQuery(
                                "select e from Employer e where e.detailed = false", Employer.class)
                        .setMaxResults(10)
                        .getResultList();

        employers.forEach(
                employer -> {
                    syncEmployerById(employer.getId());
                });
        entityManager.flush();
        return !employers.isEmpty();
    }

    @SneakyThrows
    public boolean syncVacancyDetailsBatch() {
        List<Vacancy> vacancies =
                entityManager
                        .createQuery(
                                "select e from Vacancy e where e.detailed = false", Vacancy.class)
                        .setMaxResults(100)
                        .getResultList();

        vacancies.forEach(
                vacancy -> {
                    syncVacancyById(vacancy.getId());
                });
        entityManager.flush();
        return !vacancies.isEmpty();
    }

    public void syncVacancyByDefaultFilterBatch() {
        syncVacancyByDefaultFilterBatch(DEFAULT_FILTER_PERIOD, 0);
    }

    @SneakyThrows
    public int syncVacancyByDefaultFilterBatch(String period, int page) {
        List<Map<String, String>> defaultFilter = vacancyFilterService.getDefaultFilter();

        List<Map<String, String>> updatedFilter = new ArrayList<>(defaultFilter);

        updatedFilter.add(Map.of("period", period));
        updatedFilter.add(Map.of("per_page", DEFAULT_ITEMS_PER_PAGE));
        updatedFilter.add(Map.of("page", String.valueOf(page)));

        var vacancyListResponseDto = syncVacancyFilter(updatedFilter);
        entityManager.flush();

        if (vacancyListResponseDto.getPage() < vacancyListResponseDto.getPages() - 1) {
            return vacancyListResponseDto.getPage() + 1;
        }
        return -1;
    }

    public void syncEmployerById(String id) {
        DetailedEmployerDto objects = vacancyApiClientService.loadEmployerById(id);

        if (objects.getId() == null) {
            // если не нашли работодателя, то создаем пустого
            Employer employerEmpty = EmployerUtil.getEmployerEmpty(id);
            entityManager.merge(employerEmpty);
        } else {
            Employer vacancy = entityManager.find(Employer.class, id);
            Employer employerMap = entityManager.merge(employerMapper.map(objects, vacancy));
            entityManager.merge(employerMap);
        }
    }

    public void syncVacancyById(String id) {
        DetailedVacancyDto vacancyDto = vacancyApiClientService.loadVacancyById(id);

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
    }

    private VacancyListResponseDto syncVacancyFilter(List<Map<String, String>> defaultFilter) {
        VacancyListResponseDto vacancyListResponseDto =
                vacancyApiClientService.loadVacancies(defaultFilter);

        List<String> vacancyIds =
                vacancyListResponseDto.getItems().stream().map(BriefVacancyDto::getId).toList();

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

        return vacancyListResponseDto;
    }
}
