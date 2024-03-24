package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
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
    private final EmployerRepository employerRepository;
    private final VacancyRepository vacancyRepository;

    public boolean syncEmployerDetailsBatch() {
        Slice<Employer> employers =
                employerRepository.findAllByDetailed(false, PageRequest.of(0, 10));

        employers.forEach(employer -> syncEmployerById(employer.getId()));

        return !employers.isEmpty();
    }

    @SneakyThrows
    public boolean syncVacancyDetailsBatch() {
        Slice<Vacancy> vacancies =
                vacancyRepository.findAllByDetailed(false, PageRequest.of(0, 100));

        vacancies.forEach(
                vacancy -> {
                    syncVacancyById(vacancy.getId());
                });

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
            employerRepository.save(employerEmpty);
        } else {
            Optional<Employer> optionalEmployer = employerRepository.findById(id);
            Employer employer =
                    optionalEmployer.orElseThrow(
                            () -> new EntityNotFoundException("Employer not found with id: " + id));
            employerRepository.save(employerMapper.map(objects, employer));
        }
    }

    public void syncVacancyById(String id) {
        DetailedVacancyDto vacancyDto = vacancyApiClientService.loadVacancyById(id);

        Optional<Vacancy> optionalVacancy = vacancyRepository.findById(id);

        Vacancy vacancy =
                optionalVacancy.orElseThrow(
                        () -> new EntityNotFoundException("Vacancy not found with id: " + id));

        // Workaround:
        // если при загрузке деталей вакансии получаем 404 от hh помечаем, что делали загружены
        if (vacancyDto.getId() == null) {
            vacancy.setDetailed(true);
            vacancyRepository.save(vacancy);
        } else {
            // TODO: тут если работодатель уже загружен с деталями то не нужно его обновлять
            //  затирается поле detailed

            Vacancy newVacancy = vacancyMapper.map(vacancyDto, vacancy);
            employerRepository.save(newVacancy.getEmployer());
            vacancyRepository.save(newVacancy);
        }
    }

    private VacancyListResponseDto syncVacancyFilter(List<Map<String, String>> defaultFilter) {
        VacancyListResponseDto vacancyListResponseDto =
                vacancyApiClientService.loadVacancies(defaultFilter);

        List<String> vacancyIds =
                vacancyListResponseDto.getItems().stream().map(BriefVacancyDto::getId).toList();

        // Получаем все которые ранее были загружены, чтобы обновить
        Map<String, Vacancy> vacancyMaps =
                vacancyRepository.findByIdIn(vacancyIds).stream()
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
                            employerRepository.save(vacancy.getEmployer());
                            vacancyRepository.save(vacancy);
                        });

        return vacancyListResponseDto;
    }
}
