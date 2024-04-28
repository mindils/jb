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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.ResponseWrapperSync;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;
import ru.mindils.jb.sync.dto.VacancySyncCurrentProgressDto;
import ru.mindils.jb.sync.entity.SyncLogStatus;
import ru.mindils.jb.sync.entity.SyncLogType;
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
  private final SyncLogService syncLogService;

  public VacancySyncCurrentProgressDto syncEmployerDetailsBatch() {
    Page<Employer> employers = employerRepository.findAllByDetailed(false, PageRequest.of(0, 10));

    employers.forEach(employer -> syncEmployerById(employer.getId()));

    return VacancySyncCurrentProgressDto.builder()
        .total(employers.getTotalElements())
        .finished(employers.isEmpty())
        .build();
  }

  @SneakyThrows
  public VacancySyncCurrentProgressDto syncVacancyDetailsBatch() {
    Page<Vacancy> vacancies = vacancyRepository.findAllByDetailed(false, PageRequest.of(0, 1));

    vacancies.forEach(vacancy -> {
      syncVacancyById(vacancy.getId());
    });

    return VacancySyncCurrentProgressDto.builder()
        .total(vacancies.getTotalElements())
        .finished(vacancies.isEmpty())
        .build();
  }

  public void syncVacancyByDefaultFilterBatch() {
    syncVacancyByDefaultFilterBatch(DEFAULT_FILTER_PERIOD, 0);
  }

  @SneakyThrows
  public VacancySyncCurrentProgressDto syncVacancyByDefaultFilterBatch(String period, int page) {
    List<Map<String, String>> defaultFilter = vacancyFilterService.getDefaultFilter();

    List<Map<String, String>> updatedFilter = new ArrayList<>(defaultFilter);

    updatedFilter.add(Map.of("period", period));
    updatedFilter.add(Map.of("per_page", DEFAULT_ITEMS_PER_PAGE));
    updatedFilter.add(Map.of("page", String.valueOf(page)));

    var vacancyListResponseDto = syncVacancyFilter(updatedFilter);

    if (vacancyListResponseDto.getPage() < vacancyListResponseDto.getPages() - 1) {
      return VacancySyncCurrentProgressDto.builder()
          .total(vacancyListResponseDto.getPages())
          .current(vacancyListResponseDto.getPage() + 1)
          .finished(false)
          .build();
    }
    return VacancySyncCurrentProgressDto.builder()
        .total(vacancyListResponseDto.getPages())
        .finished(true)
        .build();
  }

  public void syncEmployerById(String id) {
    var responseWrapperSync = vacancyApiClientService.loadEmployerById(id);
    var objects = responseWrapperSync.getData();

    if (objects.getId() == null) {
      // если не нашли работодателя, то создаем пустого
      Employer employerEmpty = EmployerUtil.getEmployerEmpty(id);
      employerRepository.save(employerEmpty);

      syncLogService.saveLog(
          id,
          responseWrapperSync.getResponse().body(),
          SyncLogType.EMPLOYER_DETAIL,
          SyncLogStatus.ERROR);
    } else {
      Optional<Employer> optionalEmployer = employerRepository.findById(id);
      Employer employer = optionalEmployer.orElseThrow(
          () -> new EntityNotFoundException("Employer not found with id: " + id));
      employerRepository.save(employerMapper.map(objects, employer));
      syncLogService.saveLog(
          id,
          responseWrapperSync.getResponse().body(),
          SyncLogType.EMPLOYER_DETAIL,
          SyncLogStatus.SUCCESS);
    }
  }

  public void syncVacancyById(String id) {
    var vacancyResponse = vacancyApiClientService.loadVacancyById(id);
    var vacancyDto = vacancyResponse.getData();

    Optional<Vacancy> optionalVacancy = vacancyRepository.findById(id);

    Vacancy vacancy = optionalVacancy.orElseThrow(
        () -> new EntityNotFoundException("Vacancy not found with id: " + id));

    // Workaround:
    // если при загрузке деталей вакансии получаем 404 от hh помечаем, что делали загружены
    if (vacancyDto.getId() == null) {
      vacancy.setDetailed(true);

      if (vacancyResponse.getResponse().statusCode() == HttpStatus.NOT_FOUND.value()) {
        vacancy.setArchived(true);
      }

      vacancyRepository.save(vacancy);
      syncLogService.saveLog(
          id,
          vacancyResponse.getResponse().body(),
          SyncLogType.VACANCY_DETAIL,
          SyncLogStatus.ERROR);
    } else {
      // TODO: тут если работодатель уже загружен с деталями то не нужно его обновлять
      //  затирается поле detailed

      Vacancy newVacancy = vacancyMapper.map(vacancyDto, vacancy);
      employerRepository.save(newVacancy.getEmployer());
      vacancyRepository.save(newVacancy);

      syncLogService.saveLog(
          id,
          vacancyResponse.getResponse().body(),
          SyncLogType.VACANCY_DETAIL,
          SyncLogStatus.SUCCESS);
    }
  }

  private VacancyListResponseDto syncVacancyFilter(List<Map<String, String>> defaultFilter) {
    ResponseWrapperSync<VacancyListResponseDto> vacancyListResponse =
        vacancyApiClientService.loadVacancies(defaultFilter);

    var vacancyListDto = vacancyListResponse.getData();

    List<String> vacancyIds =
        vacancyListDto.getItems().stream().map(BriefVacancyDto::getId).toList();

    // Получаем все которые ранее были загружены, чтобы обновить
    Map<String, Vacancy> vacancyMaps = vacancyRepository.findByIdIn(vacancyIds).stream()
        .collect(Collectors.toMap(Vacancy::getId, Function.identity()));

    vacancyListDto.getItems().forEach(vacancyDto -> {
      Vacancy vacancy;
      if (vacancyMaps.containsKey(vacancyDto.getId())) {
        vacancy = vacancyMapper.map(vacancyDto, vacancyMaps.get(vacancyDto.getId()));
      } else {
        vacancy = vacancyMapper.map(vacancyDto);
      }

      employerRepository.save(vacancy.getEmployer());
      vacancyRepository.save(vacancy);
    });

    return vacancyListDto;
  }
}
