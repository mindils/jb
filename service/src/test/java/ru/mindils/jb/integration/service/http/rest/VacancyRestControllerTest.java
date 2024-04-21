package ru.mindils.jb.integration.service.http.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.service.VacancyService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class VacancyRestControllerTest extends ITBase {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean
  private VacancyService vacancyService;

  @Test
  void updateStatus_Success() throws Exception {
    String vacancyId = "1";
    UpdateStatusVacancyDto updateStatusDto = new UpdateStatusVacancyDto(VacancyStatusEnum.APPROVED);
    UpdateStatusVacancyDto updatedStatusDto =
        new UpdateStatusVacancyDto(VacancyStatusEnum.APPROVED);
    when(vacancyService.updateStatus(eq(vacancyId), any(UpdateStatusVacancyDto.class)))
        .thenReturn(Optional.of(updatedStatusDto));

    String response = mockMvc
        .perform(patch("/api/v1/vacancies/{id}", vacancyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatusDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UpdateStatusVacancyDto actualUpdateStatusVacancyDto =
        objectMapper.readValue(response, UpdateStatusVacancyDto.class);
    assertThat(actualUpdateStatusVacancyDto).isEqualTo(updatedStatusDto);
  }

  @Test
  void updateStatus_NotFound() throws Exception {
    String vacancyId = "1";
    UpdateStatusVacancyDto updateStatusDto = new UpdateStatusVacancyDto(VacancyStatusEnum.APPROVED);
    when(vacancyService.updateStatus(eq(vacancyId), any(UpdateStatusVacancyDto.class)))
        .thenReturn(Optional.empty());

    mockMvc
        .perform(patch("/api/v1/vacancies/{id}", vacancyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatusDto)))
        .andExpect(status().isNotFound());
  }
}
