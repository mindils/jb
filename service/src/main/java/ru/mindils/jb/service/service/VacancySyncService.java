package ru.mindils.jb.service.service;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.dto.VacancySyncStatusDto;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
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

  public VacancySyncStatusDto getRunningStatus() {
    Optional<VacancySyncExecution> runningJob = vacancySyncExecutionService.getRunningJob();

    if (runningJob.isPresent()) {
      return VacancySyncStatusDto.builder()
          .statusText("Running job: " + runningJob.get().getStep())
          .isSyncRunning(true)
          .progress((runningJob.get().getStep().ordinal() + 1) * 20)
          .step(runningJob.get().getStep().ordinal())
          .build();
    } else {
      return VacancySyncStatusDto.builder()
          .isSyncRunning(false)
          .statusText("No running jobs")
          .progress(100)
          .step(0)
          .build();
    }
  }
}
