package ru.mindils.jb.sync.service;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncStatus;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.repository.VacancySyncExecutionRepository;

@Service
@RequiredArgsConstructor
public class VacancySyncExecutionService {

  private final VacancySyncExecutionRepository vacancySyncExecutionRepository;

  public void createNewStep(VacancySyncStep step) {
    createNewStep(step, null);
  }

  public void createNewStep(VacancySyncStep step, Map<String, ?> params) {
    var vacancyJobExecution = VacancySyncExecution.builder()
        .startTime(LocalDateTime.now())
        .parameters(params)
        .step(step)
        .status(VacancySyncStatus.NEW)
        .priority(1)
        .build();
    vacancySyncExecutionRepository.save(vacancyJobExecution);
  }

  public void completeJob(Long runningJobId) {
    var runningJob = vacancySyncExecutionRepository
        .findById(runningJobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found"));

    runningJob.setEndTime(LocalDateTime.now());
    runningJob.setStatus(VacancySyncStatus.COMPLETED);
    vacancySyncExecutionRepository.save(runningJob);
  }

  public void failedJob(Long runningJobId, Exception error) {
    var runningJob = vacancySyncExecutionRepository
        .findById(runningJobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found"));

    runningJob.setEndTime(LocalDateTime.now());
    runningJob.setStatus(VacancySyncStatus.FAILED);
    runningJob.setErrorMessage(error.toString());
    vacancySyncExecutionRepository.save(runningJob);
  }
}
