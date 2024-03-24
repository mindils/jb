package ru.mindils.jb.sync.job;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
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
    public void execute(JobExecutionContext context) {
        Optional<VacancySyncExecution> maybeRunningJob =
                vacancyJobExecutionRepository.findRunningJob();

        // если нет запущенных задач, ставим на паузу
        // тк точно знаем, что это одна задача и в следующей раз запустим ее через 3 часа
        if (maybeRunningJob.isEmpty()) {
            try {
                context.getScheduler().pauseJob(context.getJobDetail().getKey());
            } catch (SchedulerException e) {
                throw new RuntimeException(e.getMessage());
            }
            return;
        }

        var runningJob = maybeRunningJob.get();

        // разбиваем задачу на несколько шагов
        try {
            switch (runningJob.getStep()) {
                case LOAD_VACANCIES:
                    loadVacancies(runningJob);
                    break;
                case LOAD_VACANCY_DETAIL:
                    loadVacancyDetail();
                    break;
                case LOAD_EMPLOYER_DETAIL:
                    loadEmployerDetail();
                    break;
                case LOAD_VACANCY_RATING:
                    loadVacancyRating();
                    break;
            }

            vacancyJobExecutionService.completeJob(runningJob);
        } catch (Exception e) {
            vacancyJobExecutionService.failedJob(runningJob, e.getMessage());
        }
    }

    private void loadVacancyRating() {
        boolean isNextVacancyAi = syncVacancyAiService.syncVacancyAiRatingsBatch();

        if (isNextVacancyAi) {
            vacancyJobExecutionService.createNewStepVacancyAi();
        }
    }

    private void loadEmployerDetail() {
        boolean isNextEmployer = syncVacancyService.syncEmployerDetailsBatch();

        if (isNextEmployer) {
            vacancyJobExecutionService.createNewStepEmployerDetail();
        } else {
            vacancyJobExecutionService.createNewStepVacancyAi();
        }
    }

    private void loadVacancyDetail() {
        boolean isNextVacancy = syncVacancyService.syncVacancyDetailsBatch();

        if (isNextVacancy) {
            vacancyJobExecutionService.createNewStepVacancyDetail();
        } else {
            vacancyJobExecutionService.createNewStepEmployerDetail();
        }
    }

    private void loadVacancies(VacancySyncExecution runningJob) {
        var currentPage = (Integer) runningJob.getParameters().get("currentPage");
        var period = (String) runningJob.getParameters().get("period");

        int nextPage = syncVacancyService.syncVacancyByDefaultFilterBatch(period, currentPage);

        if (nextPage > 0) {
            vacancyJobExecutionService.createNewStepVacancies(
                    Map.of("currentPage", nextPage, "period", period));
        } else {
            vacancyJobExecutionService.createNewStepVacancyDetail();
        }
    }
}
