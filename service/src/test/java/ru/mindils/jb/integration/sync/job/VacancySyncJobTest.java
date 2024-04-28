package ru.mindils.jb.integration.sync.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.service.VacancySyncService;
import ru.mindils.jb.sync.dto.VacancySyncCurrentProgressDto;
import ru.mindils.jb.sync.entity.ProgressType;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncProgress;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.job.VacancySyncJob;
import ru.mindils.jb.sync.service.SyncVacancyAIService;
import ru.mindils.jb.sync.service.SyncVacancyService;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

public class VacancySyncJobTest extends ITBase {

  @Mock
  private SyncVacancyService syncVacancyService;

  @Mock
  private SyncVacancyAIService syncVacancyAiService;

  @Mock
  private VacancySyncExecutionService vacancyJobExecutionService;

  @Mock
  private VacancySyncService vacancySyncService;

  @Mock
  private JobExecutionContext context;

  @Mock
  private Scheduler scheduler;

  @InjectMocks
  private VacancySyncJob vacancySyncJob;

  @Test
  void execute_whenNoRunningJob() throws SchedulerException {
    when(vacancyJobExecutionService.getRunningJob()).thenReturn(Optional.empty());
    JobDetail jobDetail = mock(JobDetail.class);
    when(jobDetail.getKey()).thenReturn(JobKey.jobKey("vacancySyncJob", "vacancySync"));
    when(context.getJobDetail()).thenReturn(jobDetail);
    when(context.getScheduler()).thenReturn(scheduler);

    vacancySyncJob.execute(context);

    verify(scheduler).pauseJob(JobKey.jobKey("vacancySyncJob", "vacancySync"));
  }

  @Test
  void execute_whenLoadVacancies() throws JobExecutionException {
    VacancySyncExecution runningJob = VacancySyncExecution.builder()
        .id(1L)
        .step(VacancySyncStep.LOAD_VACANCIES)
        .parameters(Map.of("currentPage", 0, "period", "30"))
        .progress(VacancySyncProgress.builder()
            .total(100)
            .currentElement(0)
            .type(ProgressType.ALL)
            .build())
        .build();
    when(vacancyJobExecutionService.getRunningJob()).thenReturn(Optional.of(runningJob));
    when(syncVacancyService.syncVacancyByDefaultFilterBatch(anyString(), anyInt()))
        .thenReturn(VacancySyncCurrentProgressDto.builder()
            .total(100)
            .current(10)
            .finished(false)
            .build());

    vacancySyncJob.execute(context);

    verify(vacancyJobExecutionService)
        .createNewStep(
            eq(VacancySyncStep.LOAD_VACANCIES), anyMap(), any(VacancySyncProgress.class));
    verify(vacancyJobExecutionService).completeJob(eq(1L));
  }

  @Test
  void execute_whenLoadVacancyDetail() throws JobExecutionException {
    VacancySyncExecution runningJob = VacancySyncExecution.builder()
        .id(1L)
        .step(VacancySyncStep.LOAD_VACANCY_DETAIL)
        .progress(VacancySyncProgress.builder()
            .total(100)
            .currentElement(0)
            .type(ProgressType.ALL)
            .build())
        .build();
    when(vacancyJobExecutionService.getRunningJob()).thenReturn(Optional.of(runningJob));
    when(syncVacancyService.syncVacancyDetailsBatch())
        .thenReturn(
            VacancySyncCurrentProgressDto.builder().total(100).finished(false).build());

    vacancySyncJob.execute(context);

    verify(vacancyJobExecutionService)
        .createNewStep(
            eq(VacancySyncStep.LOAD_VACANCY_DETAIL), isNull(), any(VacancySyncProgress.class));
    verify(vacancyJobExecutionService).completeJob(eq(1L));
    verify(vacancySyncService, never()).startEmployerSync(any(ProgressType.class));
  }

  @Test
  void execute_whenLoadEmployerDetail() throws JobExecutionException {
    VacancySyncExecution runningJob = VacancySyncExecution.builder()
        .id(1L)
        .step(VacancySyncStep.LOAD_EMPLOYER_DETAIL)
        .progress(VacancySyncProgress.builder()
            .total(100)
            .currentElement(0)
            .type(ProgressType.ALL)
            .build())
        .build();
    when(vacancyJobExecutionService.getRunningJob()).thenReturn(Optional.of(runningJob));
    when(syncVacancyService.syncEmployerDetailsBatch())
        .thenReturn(
            VacancySyncCurrentProgressDto.builder().total(100).finished(false).build());

    vacancySyncJob.execute(context);

    verify(vacancyJobExecutionService)
        .createNewStep(
            eq(VacancySyncStep.LOAD_EMPLOYER_DETAIL), isNull(), any(VacancySyncProgress.class));
    verify(vacancyJobExecutionService).completeJob(eq(1L));
    verify(vacancySyncService, never()).startVacancyAiSync(any(ProgressType.class));
  }

  @Test
  void execute_whenLoadVacancyRating() throws JobExecutionException {
    VacancySyncExecution runningJob = VacancySyncExecution.builder()
        .id(1L)
        .step(VacancySyncStep.LOAD_VACANCY_RATING)
        .progress(VacancySyncProgress.builder().total(100).currentElement(0).build())
        .build();
    when(vacancyJobExecutionService.getRunningJob()).thenReturn(Optional.of(runningJob));
    when(syncVacancyAiService.syncVacancyAiRatingsBatch())
        .thenReturn(
            VacancySyncCurrentProgressDto.builder().total(100).finished(false).build());

    vacancySyncJob.execute(context);

    verify(vacancyJobExecutionService)
        .createNewStep(
            eq(VacancySyncStep.LOAD_VACANCY_RATING), isNull(), any(VacancySyncProgress.class));
    verify(vacancyJobExecutionService).completeJob(eq(1L));
  }
}
