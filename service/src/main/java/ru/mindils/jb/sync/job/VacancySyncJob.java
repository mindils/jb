package ru.mindils.jb.sync.job;

import static ru.mindils.jb.sync.entity.VacancySyncStep.LOAD_EMPLOYER_DETAIL;
import static ru.mindils.jb.sync.entity.VacancySyncStep.LOAD_VACANCIES;
import static ru.mindils.jb.sync.entity.VacancySyncStep.LOAD_VACANCY_DETAIL;
import static ru.mindils.jb.sync.entity.VacancySyncStep.LOAD_VACANCY_RATING;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import ru.mindils.jb.service.service.VacancySyncService;
import ru.mindils.jb.sync.entity.ProgressType;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncProgress;
import ru.mindils.jb.sync.service.SyncVacancyAIService;
import ru.mindils.jb.sync.service.SyncVacancyService;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@RequiredArgsConstructor
@DisallowConcurrentExecution
public class VacancySyncJob implements Job {

  private final SyncVacancyService syncVacancyService;
  private final SyncVacancyAIService syncVacancyAiService;
  private final VacancySyncExecutionService vacancyJobExecutionService;
  private final VacancySyncService vacancySyncService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Optional<VacancySyncExecution> maybeRunningJob = vacancyJobExecutionService.getRunningJob();

    if (maybeRunningJob.isEmpty()) {
      try {
        context.getScheduler().pauseJob(context.getJobDetail().getKey());
      } catch (SchedulerException e) {
        throw new JobExecutionException(e);
      }
      return;
    }

    var runningJob = maybeRunningJob.get();

    try {
      switch (runningJob.getStep()) {
        case LOAD_VACANCIES -> loadVacancies(runningJob);
        case LOAD_VACANCY_DETAIL -> loadVacancyDetail(runningJob);
        case LOAD_EMPLOYER_DETAIL -> loadEmployerDetail(runningJob);
        case LOAD_VACANCY_RATING -> loadVacancyRating(runningJob);
      }
      vacancyJobExecutionService.completeJob(runningJob.getId());
    } catch (Exception e) {
      vacancyJobExecutionService.failedJob(runningJob.getId(), e);
    }
  }

  private void loadVacancyRating(VacancySyncExecution runningJob) {
    var currentProgress = syncVacancyAiService.syncVacancyAiRatingsBatch();
    if (!currentProgress.isFinished()) {
      VacancySyncProgress progress = VacancySyncProgress.builder()
          .finished(currentProgress.isFinished())
          .currentElement(runningJob.getProgress().getTotal() - currentProgress.getTotal())
          .total(runningJob.getProgress().getTotal())
          .type(runningJob.getProgress().getType())
          .build();
      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_RATING, null, progress);
    }
  }

  private void loadEmployerDetail(VacancySyncExecution runningJob) {
    var currentProgress = syncVacancyService.syncEmployerDetailsBatch();
    if (!currentProgress.isFinished()) {
      VacancySyncProgress progress = VacancySyncProgress.builder()
          .finished(currentProgress.isFinished())
          .currentElement(runningJob.getProgress().getTotal() - currentProgress.getTotal())
          .total(runningJob.getProgress().getTotal())
          .type(runningJob.getProgress().getType())
          .build();

      vacancyJobExecutionService.createNewStep(LOAD_EMPLOYER_DETAIL, null, progress);
    } else if (runningJob.getProgress().getType() != ProgressType.ONE_STEP) {
      vacancySyncService.startVacancyAiSync(ProgressType.ALL);
    }
  }

  private void loadVacancyDetail(VacancySyncExecution runningJob) {
    var currentProgress = syncVacancyService.syncVacancyDetailsBatch();
    if (!currentProgress.isFinished()) {
      VacancySyncProgress progress = VacancySyncProgress.builder()
          .finished(currentProgress.isFinished())
          .currentElement(runningJob.getProgress().getTotal() - currentProgress.getTotal())
          .total(runningJob.getProgress().getTotal())
          .type(runningJob.getProgress().getType())
          .build();

      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_DETAIL, null, progress);
    } else if (runningJob.getProgress().getType() != ProgressType.ONE_STEP) {
      vacancySyncService.startEmployerSync(ProgressType.ALL);
    }
  }

  private void loadVacancies(VacancySyncExecution runningJob) {
    var currentPage = (Integer) runningJob.getParameters().get("currentPage");
    var period = (String) runningJob.getParameters().get("period");

    var currentProgress = syncVacancyService.syncVacancyByDefaultFilterBatch(period, currentPage);

    if (!currentProgress.isFinished()) {
      VacancySyncProgress progress = VacancySyncProgress.builder()
          .finished(currentProgress.isFinished())
          .currentElement(currentProgress.getCurrent())
          .total(currentProgress.getTotal())
          .type(runningJob.getProgress().getType())
          .build();

      vacancyJobExecutionService.createNewStep(
          LOAD_VACANCIES,
          Map.of("currentPage", currentProgress.getCurrent(), "period", period),
          progress);
    } else if (runningJob.getProgress().getType() != ProgressType.ONE_STEP) {
      vacancySyncService.startVacancySync(ProgressType.ALL);
    }
  }
}
