package ru.mindils.jb.integration.sync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.sync.dto.ResponseWrapperSync;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;
import ru.mindils.jb.sync.dto.VacancySyncCurrentProgressDto;
import ru.mindils.jb.sync.mapper.EmployerMapper;
import ru.mindils.jb.sync.mapper.VacancyMapper;
import ru.mindils.jb.sync.service.SyncLogService;
import ru.mindils.jb.sync.service.SyncVacancyService;
import ru.mindils.jb.sync.service.VacancyClientService;
import ru.mindils.jb.sync.service.VacancyFilterService;

public class SyncVacancyServiceTest extends ITBase {

  @Mock
  private VacancyClientService vacancyApiClientService;

  @Mock
  private VacancyFilterService vacancyFilterService;

  @Mock
  private VacancyMapper vacancyMapper;

  @Mock
  private EmployerRepository employerRepository;

  @Mock
  private VacancyRepository vacancyRepository;

  @Mock
  private SyncLogService syncLogService;

  @Mock
  private EmployerMapper employerMapper;

  @InjectMocks
  private SyncVacancyService syncVacancyService;

  @Test
  public void syncEmployerDetailsBatch() {
    String employerId = "employer-id-example";
    Employer employer = getEmployer(employerId);

    Page<Employer> employerPage = new PageImpl<>(List.of(employer));

    when(employerRepository.findAllByDetailed(false, PageRequest.of(0, 10)))
        .thenReturn(employerPage);

    DetailedEmployerDto detailedEmployerDto = new DetailedEmployerDto();
    detailedEmployerDto.setId(employerId);

    HttpResponse<String> httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("response body");

    ResponseWrapperSync<DetailedEmployerDto> responseWrapper =
        ResponseWrapperSync.<DetailedEmployerDto>builder()
            .data(detailedEmployerDto)
            .response(httpResponse)
            .build();
    when(vacancyApiClientService.loadEmployerById(employerId)).thenReturn(responseWrapper);

    when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));

    when(employerMapper.map(detailedEmployerDto, employer)).thenReturn(employer);

    VacancySyncCurrentProgressDto result = syncVacancyService.syncEmployerDetailsBatch();

    verify(employerRepository).findAllByDetailed(false, PageRequest.of(0, 10));
    verify(syncLogService, times(1)).saveLog(eq(employerId), anyString(), any(), any());

    assertThat(result.getTotal()).isEqualTo(1);
    assertThat(result.getCurrent()).isEqualTo(0);
    assertThat(result.isFinished()).isFalse();
  }

  @Test
  public void syncVacancyDetailsBatch() {
    String vacancyId = "vacancy-id-example";
    String employerId = "employer-id-example";
    Employer employer = getEmployer(employerId);
    Vacancy vacancy = getVacancy(vacancyId, employer);

    Page<Vacancy> vacancyPage = new PageImpl<>(List.of(vacancy));

    when(vacancyRepository.findAllByDetailed(false, PageRequest.of(0, 1))).thenReturn(vacancyPage);

    DetailedVacancyDto detailedVacancyDto = new DetailedVacancyDto();
    detailedVacancyDto.setId(vacancyId);

    HttpResponse<String> httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("response body");

    ResponseWrapperSync<DetailedVacancyDto> responseWrapper =
        ResponseWrapperSync.<DetailedVacancyDto>builder()
            .data(detailedVacancyDto)
            .response(httpResponse)
            .build();
    when(vacancyApiClientService.loadVacancyById(vacancyId)).thenReturn(responseWrapper);

    when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

    Vacancy mappedVacancy = getVacancy(vacancyId, employer);
    when(vacancyMapper.map(detailedVacancyDto, vacancy)).thenReturn(mappedVacancy);

    VacancySyncCurrentProgressDto result = syncVacancyService.syncVacancyDetailsBatch();

    verify(vacancyRepository).findAllByDetailed(false, PageRequest.of(0, 1));
    verify(syncLogService, times(1)).saveLog(eq(vacancyId), anyString(), any(), any());

    assertThat(result.getTotal()).isEqualTo(1);
    assertThat(result.getCurrent()).isEqualTo(0);
    assertThat(result.isFinished()).isFalse();
  }

  @Test
  public void syncVacancyByDefaultFilterBatch() {
    List<Map<String, String>> defaultFilter = List.of(Map.of("test", "value"));

    String vacancyId = "vacancy-id-example";
    String employerId = "employer-id-example";
    Employer employer = getEmployer(employerId);
    Vacancy vacancy = getVacancy(vacancyId, employer);

    BriefVacancyDto briefVacancyDto = new BriefVacancyDto();
    briefVacancyDto.setId(vacancyId);

    VacancyListResponseDto responseDto = new VacancyListResponseDto();
    responseDto.setPages(2L);
    responseDto.setPage(0);
    responseDto.setItems(List.of(briefVacancyDto));

    HttpResponse<String> httpResponse = mock(HttpResponse.class);
    when(httpResponse.body()).thenReturn("response body");

    ResponseWrapperSync<VacancyListResponseDto> responseWrapper =
        ResponseWrapperSync.<VacancyListResponseDto>builder()
            .data(responseDto)
            .response(httpResponse)
            .build();

    when(vacancyFilterService.getDefaultFilter()).thenReturn(defaultFilter);
    when(vacancyApiClientService.loadVacancies(anyList())).thenReturn(responseWrapper);

    when(vacancyRepository.findByIdIn(List.of(vacancyId))).thenReturn(List.of(vacancy));

    Vacancy mappedVacancy = getVacancy(vacancyId, employer);
    when(vacancyMapper.map(briefVacancyDto, vacancy)).thenReturn(mappedVacancy);

    VacancySyncCurrentProgressDto result =
        syncVacancyService.syncVacancyByDefaultFilterBatch("30", 0);

    verify(vacancyFilterService).getDefaultFilter();
    verify(vacancyApiClientService).loadVacancies(anyList());

    assertThat(result.getTotal()).isEqualTo(2);
    assertThat(result.getCurrent()).isEqualTo(1);
    assertThat(result.isFinished()).isFalse();
  }

  private static Employer getEmployer(String id) {
    return Employer.builder()
        .id(id)
        .name("ООО Рога и копыта")
        .trusted(true)
        .description("Описание работодателя")
        .detailed(true)
        .createdAt(Instant.now())
        .build();
  }

  private static Vacancy getVacancy(String id, Employer employer) {
    return Vacancy.builder()
        .id(id)
        .name("Разработчик Java")
        .employer(employer)
        .premium(false)
        .city("Москва")
        .salary(
            Salary.builder().from(100000).to(150000).currency("RUR").gross(true).build())
        .type("open")
        .publishedAt(Instant.now())
        .createdAt(Instant.now())
        .archived(false)
        .applyAlternateUrl("https://example.com")
        .url("https://example.com")
        .alternateUrl("https://example.com")
        .schedule("fullDay")
        .responseUrl("https://example.com")
        .professionalRoles(List.of(Map.of("id", "1", "name", "Java Developer")))
        .employment("full")
        .description("Описание вакансии")
        .build();
  }
}
