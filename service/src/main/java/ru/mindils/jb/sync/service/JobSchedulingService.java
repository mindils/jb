package ru.mindils.jb.sync.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;
import ru.mindils.jb.sync.job.EmployerDetailsSyncJob;
import ru.mindils.jb.sync.job.VacancyAiSyncJob;
import ru.mindils.jb.sync.job.VacancyAllSyncJob;
import ru.mindils.jb.sync.job.VacancyDetailsSyncJob;

@Service
@RequiredArgsConstructor
public class JobSchedulingService {

    private static final String VACANCY_SYNC_GROUP = "vacancySync";
    private final Scheduler scheduler;

    @SneakyThrows
    public void scheduleVacancyAiSyncJob() {
        JobDetail jobDetail =
                JobBuilder.newJob(VacancyAiSyncJob.class)
                        .withIdentity("vacancyAiSyncJob", VACANCY_SYNC_GROUP)
                        .build();

        Trigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity("vacancyAiSyncJobTrigger", VACANCY_SYNC_GROUP)
                        .startNow()
                        .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @SneakyThrows
    public void scheduleVacancyAllSyncJob() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("page", 0);
        jobDataMap.put("period", "1");

        JobDetail jobDetail =
                JobBuilder.newJob(VacancyAllSyncJob.class)
                        .withIdentity("vacancyAllSyncJob", VACANCY_SYNC_GROUP)
                        .build();

        Trigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity("vacancyAllSyncJobTrigger", VACANCY_SYNC_GROUP)
                        .usingJobData(jobDataMap)
                        .startAt(new Date(System.currentTimeMillis() + 1000))
                        .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @SneakyThrows
    public void scheduleVacancyDetailSyncJob() {
        JobDetail jobDetail =
                JobBuilder.newJob(VacancyDetailsSyncJob.class)
                        .withIdentity("vacancyDetailsSyncJob", VACANCY_SYNC_GROUP)
                        .build();

        Trigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity("vacancyDetailsSyncJobTrigger", VACANCY_SYNC_GROUP)
                        .startAt(new Date(System.currentTimeMillis() + 1000))
                        .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @SneakyThrows
    public void scheduleEmployerDetailSyncJob() {
        JobDetail jobDetail =
                JobBuilder.newJob(EmployerDetailsSyncJob.class)
                        .withIdentity("vacancyDetailsSyncJob", VACANCY_SYNC_GROUP)
                        .build();

        Trigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity("vacancyDetailsSyncJobTrigger", VACANCY_SYNC_GROUP)
                        .startAt(new Date(System.currentTimeMillis() + 1000))
                        .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
