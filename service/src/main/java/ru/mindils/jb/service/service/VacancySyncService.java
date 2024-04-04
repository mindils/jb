package ru.mindils.jb.service.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@Service
@RequiredArgsConstructor
public class VacancySyncService {

  private final VacancySyncExecutionService vacancySyncExecutionService;
  private final Scheduler scheduler;

  @SneakyThrows
  public void startAllSync(String syncPeriod) {
    vacancySyncExecutionService.createNewStep(
        VacancySyncStep.LOAD_VACANCIES, Map.of("currentPage", 0, "period", syncPeriod));
    scheduler.resumeJob(JobKey.jobKey("vacancySyncJob", "vacancySync"));
  }

  public boolean isSyncRunning() {
    return vacancySyncExecutionService.getRunningJob().isPresent();
  }
}
