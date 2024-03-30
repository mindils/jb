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

  public void createNewStepVacancyDetail() {
    createNewStep(VacancySyncStep.LOAD_VACANCY_DETAIL, null);
  }

  public void createNewStepVacancies(Map<String, ?> params) {
    createNewStep(VacancySyncStep.LOAD_VACANCIES, params);
  }

  public void createNewStepEmployerDetail() {
    createNewStep(VacancySyncStep.LOAD_EMPLOYER_DETAIL, null);
  }

  public void createNewStepVacancyAi() {
    createNewStep(VacancySyncStep.LOAD_VACANCY_RATING, null);
  }

  public void createNewStep(VacancySyncStep step, Map<String, ?> params) {
    var vacancyJobExecution = VacancySyncExecution.builder()
        .startTime(LocalDateTime.now())
        .parameters(params)
        .step(step)
        .status(VacancySyncStatus.RUNNING)
        .priority(1)
        .build();

    vacancySyncExecutionRepository.save(vacancyJobExecution);
  }

  public void completeJob(VacancySyncExecution runningJob) {
    runningJob.setEndTime(LocalDateTime.now());
    runningJob.setStatus(VacancySyncStatus.COMPLETED);
    vacancySyncExecutionRepository.save(runningJob);
  }

  public void failedJob(VacancySyncExecution runningJob, String errorMessage) {
    runningJob.setEndTime(LocalDateTime.now());
    runningJob.setStatus(VacancySyncStatus.FAILED);
    runningJob.setErrorMessage(errorMessage);
    vacancySyncExecutionRepository.save(runningJob);
  }
}
