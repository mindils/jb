package ru.mindils.jb.sync.job;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import ru.mindils.jb.sync.service.SyncVacancyService;

@RequiredArgsConstructor
public class EmployerDetailsSyncJob implements Job {

    private final SyncVacancyService syncVacancyService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean hasMoreVacancies = syncVacancyService.syncEmployerDetailsBatch();

        if (hasMoreVacancies) {
            // Планируем следующий запуск немедленно или с небольшой задержкой
            JobDetail jobDetail = context.getJobDetail();
            Trigger trigger =
                    TriggerBuilder.newTrigger()
                            .forJob(jobDetail)
                            .startAt(new Date(System.currentTimeMillis() + 1000))
                            .build();

            try {
                context.getScheduler().scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new JobExecutionException(e);
            }
        }
    }
}
