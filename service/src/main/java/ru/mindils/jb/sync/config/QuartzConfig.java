package ru.mindils.jb.sync.config;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mindils.jb.sync.job.VacancySyncJob;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail vacancySyncJobDetail() {
        return JobBuilder.newJob(VacancySyncJob.class)
                .withIdentity("vacancySyncJob", "vacancySync")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger vacancySyncTrigger(JobDetail vacancySyncJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(vacancySyncJobDetail)
                .withIdentity("vacancySyncTrigger", "vacancySync")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(1)
                                .repeatForever())
                .build();
    }
}
