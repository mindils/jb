package ru.mindils.jb.sync.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import ru.mindils.jb.sync.service.SyncVacancyAIService;

@RequiredArgsConstructor
public class VacancyAiSyncJob implements Job {

    private final SyncVacancyAIService syncVacancyAIService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean hasMoreVacancies = syncVacancyAIService.syncVacancyAiRatingsBatch();

        if (hasMoreVacancies) {
            JobDetail jobDetail = context.getJobDetail();
            Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail).startNow().build();

            try {
                context.getScheduler().scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new JobExecutionException(e);
            }
        }
    }
}
