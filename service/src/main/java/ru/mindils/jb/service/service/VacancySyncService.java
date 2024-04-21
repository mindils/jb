package ru.mindils.jb.service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.dto.VacancySyncStatusDto;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyInfoRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.sync.entity.ProgressType;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncProgress;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@Service
@RequiredArgsConstructor
@Transactional
public class VacancySyncService {

  private final VacancySyncExecutionService vacancySyncExecutionService;
  private final EmployerRepository employerRepository;
  private final VacancyRepository vacancyRepository;
  private final VacancyInfoRepository vacancyInfoRepository;
  private final Scheduler scheduler;

  @SneakyThrows
  public void startAllSync(String syncPeriod) {
    vacancySyncExecutionService.createNewStep(
        VacancySyncStep.LOAD_VACANCIES, Map.of("currentPage", 0, "period", syncPeriod));
    resumeJob();
  }

  public void startVacancySync(ProgressType progressType) {
    var totalCount = vacancyRepository.countVacanciesDetailedFalse();
    vacancySyncExecutionService.createNewStep(
        VacancySyncStep.LOAD_VACANCY_DETAIL,
        null,
        VacancySyncProgress.builder()
            .total(totalCount)
            .currentElement(0)
            .type(progressType)
            .build());
    resumeJob();
  }

  public void startVacancySync(LocalDate syncDate) {
    vacancyRepository.updateDetailedBy(syncDate);
    startVacancySync(ProgressType.ONE_STEP);
  }

  public void startVacancyAiSync() {
    startVacancyAiSync(ProgressType.ONE_STEP);
  }

  public void startVacancyAiSync(ProgressType progressType) {
    vacancyInfoRepository.updateAiApprovedToNullByThreshold(LocalDateTime.now());
    var totalCount = vacancyRepository.countVacanciesWithoutAiApproved();
    vacancySyncExecutionService.createNewStep(
        VacancySyncStep.LOAD_VACANCY_RATING,
        null,
        VacancySyncProgress.builder()
            .total(totalCount)
            .currentElement(0)
            .type(progressType)
            .build());
    resumeJob();
  }

  public void startEmployerSync() {
    startEmployerSync(ProgressType.ONE_STEP);
  }

  public void startEmployerSync(ProgressType progressType) {
    var totalCount = employerRepository.countEmployerWithoutDetailed();

    vacancySyncExecutionService.createNewStep(
        VacancySyncStep.LOAD_EMPLOYER_DETAIL,
        null,
        VacancySyncProgress.builder()
            .total(totalCount)
            .currentElement(0)
            .type(progressType)
            .build());
    resumeJob();
  }

  public void startEmployerSync(LocalDate date) {
    employerRepository.updateDetailedBy(date);
    startEmployerSync(ProgressType.ONE_STEP);
  }

  public boolean isSyncRunning() {
    return vacancySyncExecutionService.getRunningJob().isPresent();
  }

  public VacancySyncStatusDto getRunningStatus() {
    return vacancySyncExecutionService
        .getRunningJob()
        .map(this::calculateStatus)
        .orElseGet(this::noRunningJobsStatus);
  }

  @SneakyThrows
  private void resumeJob() {
    scheduler.resumeJob(JobKey.jobKey("vacancySyncJob", "vacancySync"));
  }

  private VacancySyncStatusDto calculateStatus(VacancySyncExecution job) {
    if (job.getProgress() != null) {
      return buildStatusDto(job, calculateProgressPercentage(job));
    } else {
      return buildInitialStatusDto(job);
    }
  }

  private int calculateProgressPercentage(VacancySyncExecution job) {
    VacancySyncProgress progress = job.getProgress();

    if (progress == null || progress.getTotal() == 0) {
      return 0;
    }

    long currentElement = progress.getCurrentElement();

    double remaining = (double) currentElement / progress.getTotal() * 100;

    if (ProgressType.ONE_STEP.equals(progress.getType())) {
      return (int) remaining;
    } else {
      int step = job.getStep().ordinal();
      if (step > 3) {
        return 0;
      }
      int base = step * 25;

      double stepProgress = 25 * (remaining / 100);
      return base + (int) stepProgress;
    }
  }

  private VacancySyncStatusDto buildStatusDto(VacancySyncExecution job, int progress) {
    return VacancySyncStatusDto.builder()
        .statusText("(%s%%) Running job: %s".formatted(progress, job.getStep()))
        .syncRunning(true)
        .progress(progress)
        .currentElement(job.getProgress().getCurrentElement())
        .totalElements(job.getProgress().getTotal())
        .step(job.getStep().ordinal())
        .build();
  }

  private VacancySyncStatusDto buildInitialStatusDto(VacancySyncExecution job) {
    return VacancySyncStatusDto.builder()
        .statusText("Running job: %s".formatted(job.getStep()))
        .syncRunning(true)
        .progress((job.getStep().ordinal() + 1) * 5) // Presuming some initial step calculation
        .step(job.getStep().ordinal())
        .build();
  }

  private VacancySyncStatusDto noRunningJobsStatus() {
    return VacancySyncStatusDto.builder()
        .syncRunning(false)
        .statusText("No running jobs")
        .progress(100)
        .step(0)
        .build();
  }
}
