package ru.mindils.jb.integration.service.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.service.VacancyService;

@RequiredArgsConstructor
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private VacancyService vacancyService;

  @Test
  public void testHandleOptimisticLockingFailureException() throws Exception {
    String vacancyId = "123";

    when(vacancyService.updateStatus(eq(vacancyId), any(UpdateStatusVacancyDto.class)))
        .thenThrow(new OptimisticLockingFailureException(
            "Vacancy status has been updated by another user"));

    mockMvc
        .perform(patch("/api/v1/vacancies/{id}", vacancyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"APPROVED\"}"))
        .andExpect(status().isConflict());
  }
}
