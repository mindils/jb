package ru.mindils.jb.integration.sync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.sync.entity.VacancySyncExecution;
import ru.mindils.jb.sync.entity.VacancySyncProgress;
import ru.mindils.jb.sync.entity.VacancySyncStatus;
import ru.mindils.jb.sync.entity.VacancySyncStep;
import ru.mindils.jb.sync.repository.VacancySyncExecutionRepository;
import ru.mindils.jb.sync.service.VacancySyncExecutionService;

@RequiredArgsConstructor
class VacancySyncExecutionServiceTest extends ITBase {

  @Mock
  private VacancySyncExecutionRepository vacancySyncExecutionRepository;

  @InjectMocks
  private VacancySyncExecutionService vacancySyncExecutionService;

  @Test
  void createNewStep_withStepOnly() {
    VacancySyncStep step = VacancySyncStep.LOAD_VACANCIES;

    vacancySyncExecutionService.createNewStep(step);

    verify(vacancySyncExecutionRepository, times(1)).save(argThat(execution -> {
      assertThat(execution.getStep()).isEqualTo(step);
      assertThat(execution.getStatus()).isEqualTo(VacancySyncStatus.NEW);
      assertThat(execution.getPriority()).isEqualTo(1);
      return true;
    }));
  }

  @Test
  void createNewStep_withStepAndParams() {
    VacancySyncStep step = VacancySyncStep.LOAD_VACANCIES;
    Map<String, Object> params = Map.of("param1", "value1", "param2", "value2");

    vacancySyncExecutionService.createNewStep(step, params);

    verify(vacancySyncExecutionRepository, times(1)).save(argThat(execution -> {
      assertThat(execution.getStep()).isEqualTo(step);
      assertThat(execution.getStatus()).isEqualTo(VacancySyncStatus.NEW);
      assertThat(execution.getPriority()).isEqualTo(1);
      assertThat(execution.getParameters()).isEqualTo(params);
      return true;
    }));
  }

  @Test
  void createNewStep_withStepParamsAndProgress() {
    VacancySyncStep step = VacancySyncStep.LOAD_VACANCIES;
    Map<String, Object> params = Map.of("param1", "value1", "param2", "value2");
    VacancySyncProgress progress = VacancySyncProgress.builder().total(100).build();

    vacancySyncExecutionService.createNewStep(step, params, progress);

    verify(vacancySyncExecutionRepository, times(1)).save(argThat(execution -> {
      assertThat(execution.getStep()).isEqualTo(step);
      assertThat(execution.getStatus()).isEqualTo(VacancySyncStatus.NEW);
      assertThat(execution.getPriority()).isEqualTo(1);
      assertThat(execution.getParameters()).isEqualTo(params);
      assertThat(execution.getProgress()).isEqualTo(progress);
      return true;
    }));
  }

  @Test
  void getRunningJob_jobExists() {
    VacancySyncExecution runningJob = new VacancySyncExecution();
    when(vacancySyncExecutionRepository.findRunningJob()).thenReturn(Optional.of(runningJob));

    Optional<VacancySyncExecution> result = vacancySyncExecutionService.getRunningJob();

    assertThat(result).isPresent().contains(runningJob);
  }

  @Test
  void getRunningJob_jobDoesNotExist() {
    when(vacancySyncExecutionRepository.findRunningJob()).thenReturn(Optional.empty());

    Optional<VacancySyncExecution> result = vacancySyncExecutionService.getRunningJob();

    assertThat(result).isEmpty();
  }

  @Test
  void completeJob_jobExists() {
    Long runningJobId = 1L;
    VacancySyncExecution runningJob = new VacancySyncExecution();
    runningJob.setId(runningJobId);
    when(vacancySyncExecutionRepository.findById(runningJobId)).thenReturn(Optional.of(runningJob));

    vacancySyncExecutionService.completeJob(runningJobId);

    assertThat(runningJob.getEndTime()).isNotNull();
    assertThat(runningJob.getStatus()).isEqualTo(VacancySyncStatus.COMPLETED);
    verify(vacancySyncExecutionRepository, times(1)).save(runningJob);
  }

  @Test
  void completeJob_jobDoesNotExist() {
    Long runningJobId = 1L;
    when(vacancySyncExecutionRepository.findById(runningJobId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> {
      vacancySyncExecutionService.completeJob(runningJobId);
    });
    verify(vacancySyncExecutionRepository, never()).save(any());
  }

  @Test
  void failedJob_jobExists() {
    Long runningJobId = 1L;
    VacancySyncExecution runningJob = new VacancySyncExecution();
    runningJob.setId(runningJobId);
    when(vacancySyncExecutionRepository.findById(runningJobId)).thenReturn(Optional.of(runningJob));
    Exception error = new RuntimeException("Test error");

    vacancySyncExecutionService.failedJob(runningJobId, error);

    assertThat(runningJob.getEndTime()).isNotNull();
    assertThat(runningJob.getStatus()).isEqualTo(VacancySyncStatus.FAILED);
    assertThat(runningJob.getErrorMessage()).isEqualTo(error.toString());
    verify(vacancySyncExecutionRepository, times(1)).save(runningJob);
  }

  @Test
  void failedJob_jobDoesNotExist() {
    Long runningJobId = 1L;
    when(vacancySyncExecutionRepository.findById(runningJobId)).thenReturn(Optional.empty());
    Exception error = new RuntimeException("Test error");

    assertThrows(IllegalArgumentException.class, () -> {
      vacancySyncExecutionService.failedJob(runningJobId, error);
    });
    verify(vacancySyncExecutionRepository, never()).save(any());
  }
}
