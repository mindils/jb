package ru.mindils.jb.sync.job;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import ru.mindils.jb.sync.service.SyncVacancyService;

@RequiredArgsConstructor
public class VacancyAllSyncJob implements Job {

    private final SyncVacancyService syncVacancyService;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getTrigger().getJobDataMap();
        int page = dataMap.getInt("page");
        String period = dataMap.getString("period");

        int nextPage = syncVacancyService.syncVacancyByDefaultFilterBatch(period, page);

        if (nextPage >= 0) {
            dataMap.put("page", nextPage);
            Trigger trigger =
                    TriggerBuilder.newTrigger()
                            .forJob(context.getJobDetail())
                            .usingJobData(dataMap)
                            .startAt(new Date(System.currentTimeMillis() + 1000))
                            .build();

            context.getScheduler().scheduleJob(trigger);
        }
    }
}
