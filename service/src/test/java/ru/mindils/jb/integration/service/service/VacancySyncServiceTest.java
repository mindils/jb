package ru.mindils.jb.integration.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.VacancySyncStatusDto;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyInfoRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.service.service.VacancySyncService;
import ru.mindils.jb.sync.entity.ProgressType;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@ExtendWith(MockitoExtension.class)
public class VacancySyncServiceTest extends ITBase {

  @Mock
  private VacancySyncExecutionService vacancySyncExecutionService;

  @Mock
  private Scheduler scheduler;

  @Mock
  private VacancyRepository vacancyRepository;

  @Mock
  private VacancyInfoRepository vacancyInfoRepository;

  @Mock
  private EmployerRepository employerRepository;

  @InjectMocks
  private VacancySyncService vacancySyncService;

  @BeforeEach
  void setUp() {
    vacancySyncService = new VacancySyncService(
        vacancySyncExecutionService,
        employerRepository,
        vacancyRepository,
        vacancyInfoRepository,
        scheduler);
  }

  @Test
  void startAllSync() throws SchedulerException {
    String syncPeriod = "30";

    vacancySyncService.startAllSync(syncPeriod);

    verify(vacancySyncExecutionService, times(1))
        .createNewStep(
            eq(VacancySyncStep.LOAD_VACANCIES), eq(Map.of("currentPage", 0, "period", syncPeriod)));
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void startVacancySync() throws SchedulerException {
    when(vacancyRepository.countVacanciesDetailedFalse()).thenReturn(10L);

    vacancySyncService.startVacancySync(ProgressType.ONE_STEP);

    verify(vacancySyncExecutionService, times(1))
        .createNewStep(eq(VacancySyncStep.LOAD_VACANCY_DETAIL), eq(null), any());
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void startVacancySync_withDate() throws SchedulerException {
    LocalDate syncDate = LocalDate.now();

    vacancySyncService.startVacancySync(syncDate);

    verify(vacancyRepository, times(1)).updateDetailedBy(syncDate);
    verify(vacancySyncExecutionService, times(1))
        .createNewStep(eq(VacancySyncStep.LOAD_VACANCY_DETAIL), eq(null), any());
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void startVacancyAiSync() throws SchedulerException {
    when(vacancyRepository.countVacanciesWithoutAiApproved()).thenReturn(10L);

    vacancySyncService.startVacancyAiSync();

    verify(vacancyInfoRepository, times(1)).updateAiApprovedToNullByThreshold(any());
    verify(vacancySyncExecutionService, times(1))
        .createNewStep(eq(VacancySyncStep.LOAD_VACANCY_RATING), eq(null), any());
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void startEmployerSync() throws SchedulerException {
    when(employerRepository.countEmployerWithoutDetailed()).thenReturn(10L);

    vacancySyncService.startEmployerSync();

    verify(vacancySyncExecutionService, times(1))
        .createNewStep(eq(VacancySyncStep.LOAD_EMPLOYER_DETAIL), eq(null), any());
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void startEmployerSync_withDate() throws SchedulerException {
    LocalDate syncDate = LocalDate.now();

    vacancySyncService.startEmployerSync(syncDate);

    verify(employerRepository, times(1)).updateDetailedBy(syncDate);
    verify(vacancySyncExecutionService, times(1))
        .createNewStep(eq(VacancySyncStep.LOAD_EMPLOYER_DETAIL), eq(null), any());
    verify(scheduler, times(1)).resumeJob(any());
  }

  @Test
  void isSyncRunning_true() {
    VacancySyncExecution syncExecution = new VacancySyncExecution();
    when(vacancySyncExecutionService.getRunningJob()).thenReturn(Optional.of(syncExecution));

    boolean result = vacancySyncService.isSyncRunning();

    assertThat(result).isTrue();
  }

  @Test
  void isSyncRunning_false() {
    when(vacancySyncExecutionService.getRunningJob()).thenReturn(Optional.empty());

    boolean result = vacancySyncService.isSyncRunning();

    assertThat(result).isFalse();
  }

  @Test
  void getRunningStatus_syncRunning() {
    VacancySyncExecution syncExecution = new VacancySyncExecution();
    syncExecution.setStep(VacancySyncStep.LOAD_VACANCIES);
    when(vacancySyncExecutionService.getRunningJob()).thenReturn(Optional.of(syncExecution));

    VacancySyncStatusDto result = vacancySyncService.getRunningStatus();

    assertThat(result.getSyncRunning()).isTrue();
    assertThat(result.getStatusText()).contains("Running job: LOAD_VACANCIES");
    assertThat(result.getProgress()).isBetween(0, 100);
    assertThat(result.getStep()).isEqualTo(VacancySyncStep.LOAD_VACANCIES.ordinal());
  }

  @Test
  void getRunningStatus_noSyncRunning() {
    when(vacancySyncExecutionService.getRunningJob()).thenReturn(Optional.empty());

    VacancySyncStatusDto result = vacancySyncService.getRunningStatus();

    assertThat(result.getSyncRunning()).isFalse();
    assertThat(result.getStatusText()).isEqualTo("No running jobs");
    assertThat(result.getProgress()).isEqualTo(100);
    assertThat(result.getStep()).isZero();
  }
}
