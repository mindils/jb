package ru.mindils.jb.integration.service.exception;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.exception.GlobalExceptionHandler;
import ru.mindils.jb.service.service.UserService;
import ru.mindils.jb.service.service.VacancyService;

@RequiredArgsConstructor
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private VacancyService vacancyService;

  @MockBean
  private UserService userService;

  @Test
  void testHandleOptimisticLockingFailureException() throws Exception {
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

  @Test
  void handleEntityNotFoundException() throws Exception {
    String errorMessage = "Entity not found";
    EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

    when(userService.findById(1L)).thenThrow(exception);

    mockMvc
        .perform(get("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(errorMessage));

    verify(userService).findById(any());
  }

  @Test
  void handleMethodArgumentNotValidException() throws Exception {
    BindingResult bindingResult =
        new BeanPropertyBindingResult(RegistrationDto.builder().build(), "registrationDto");
    bindingResult.addError(new FieldError("registrationDto", "username", "Логин обязателен"));
    bindingResult.addError(new FieldError("registrationDto", "password", "Пароль обязателен"));

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

    GlobalExceptionHandler exceptionHandler = mock(GlobalExceptionHandler.class);
    when(exceptionHandler.handleBadRequest(any(MethodArgumentNotValidException.class)))
        .thenCallRealMethod();

    mockMvc
        .perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Логин обязателен")))
        .andExpect(content().string(containsString("Пароль обязателен")));

    verify(userService, never()).create(any(RegistrationDto.class));
  }

  @Test
  void handleHttpMessageNotReadableException() throws Exception {
    HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
    when(exception.getMessage()).thenReturn("Invalid JSON");

    mockMvc
        .perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"john\", \"password\": \"123\""))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("JSON parse error")));

    verify(userService, never()).create(any(RegistrationDto.class));
  }

  @Test
  void handleDataAccessException() throws Exception {
    DataAccessException exception = mock(DataAccessException.class);

    when(userService.findById(anyLong())).thenThrow(exception);

    mockMvc
        .perform(get("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Database error occurred"));

    verify(userService).findById(1L);
  }
}
