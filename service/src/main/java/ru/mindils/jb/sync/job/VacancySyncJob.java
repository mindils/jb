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
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.repository.VacancySyncExecutionRepository;
import ru.mindils.jb.sync.service.SyncVacancyAIService;
import ru.mindils.jb.sync.service.SyncVacancyService;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@RequiredArgsConstructor
@DisallowConcurrentExecution
public class VacancySyncJob implements Job {

  private final VacancySyncExecutionRepository vacancyJobExecutionRepository;
  private final SyncVacancyService syncVacancyService;
  private final SyncVacancyAIService syncVacancyAiService;
  private final VacancySyncExecutionService vacancyJobExecutionService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    Optional<VacancySyncExecution> maybeRunningJob = vacancyJobExecutionRepository.findRunningJob();

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
        case LOAD_VACANCY_DETAIL -> loadVacancyDetail();
        case LOAD_EMPLOYER_DETAIL -> loadEmployerDetail();
        case LOAD_VACANCY_RATING -> loadVacancyRating();
      }
      vacancyJobExecutionService.completeJob(runningJob.getId());
    } catch (Exception e) {
      vacancyJobExecutionService.failedJob(runningJob.getId(), e);
    }
  }

  private void loadVacancyRating() {
    boolean isNextVacancyAi = syncVacancyAiService.syncVacancyAiRatingsBatch();
    if (isNextVacancyAi) {
      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_RATING);
    }
  }

  private void loadEmployerDetail() {
    boolean isNextEmployer = syncVacancyService.syncEmployerDetailsBatch();
    if (isNextEmployer) {
      vacancyJobExecutionService.createNewStep(LOAD_EMPLOYER_DETAIL);
    } else {
      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_RATING);
    }
  }

  private void loadVacancyDetail() {
    boolean isNextVacancy = syncVacancyService.syncVacancyDetailsBatch();
    if (isNextVacancy) {
      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_DETAIL);
    } else {
      vacancyJobExecutionService.createNewStep(LOAD_EMPLOYER_DETAIL);
    }
  }

  private void loadVacancies(VacancySyncExecution runningJob) {
    var currentPage = (Integer) runningJob.getParameters().get("currentPage");
    var period = (String) runningJob.getParameters().get("period");
    int nextPage = syncVacancyService.syncVacancyByDefaultFilterBatch(period, currentPage);
    if (nextPage > 0) {
      vacancyJobExecutionService.createNewStep(
          LOAD_VACANCIES, Map.of("currentPage", nextPage, "period", period));
    } else {
      vacancyJobExecutionService.createNewStep(LOAD_VACANCY_DETAIL);
    }
  }
}
