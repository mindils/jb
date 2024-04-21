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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.EmployerReadDto;
import ru.mindils.jb.service.dto.EmployerUpdateStatusDto;
import ru.mindils.jb.service.entity.EmployerStatusEnum;
import ru.mindils.jb.service.service.EmployerService;

@Nested
@AutoConfigureMockMvc
@RequiredArgsConstructor
class EmployerRestControllerTest extends ITBase {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean
  private EmployerService employerService;

  @Test
  void updateStatus_Success() throws Exception {
    String employerId = "1";
    EmployerUpdateStatusDto updateStatusDto =
        new EmployerUpdateStatusDto(EmployerStatusEnum.APPROVED);
    EmployerReadDto employerReadDto = new EmployerReadDto(
        employerId,
        "Employer 1",
        true,
        "Description 1",
        true,
        false,
        null,
        null,
        null,
        null,
        null,
        null);

    when(employerService.updateStatus(eq(employerId), any(EmployerUpdateStatusDto.class)))
        .thenReturn(Optional.of(employerReadDto));

    String response = mockMvc
        .perform(patch("/api/v1/employers/{id}/status", employerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatusDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EmployerReadDto actualEmployerReadDto = objectMapper.readValue(response, EmployerReadDto.class);

    assertThat(actualEmployerReadDto).isEqualTo(employerReadDto);
  }

  @Test
  void updateStatus_NotFound() throws Exception {
    String employerId = "1";
    EmployerUpdateStatusDto updateStatusDto =
        new EmployerUpdateStatusDto(EmployerStatusEnum.APPROVED);
    when(employerService.updateStatus(eq(employerId), any(EmployerUpdateStatusDto.class)))
        .thenReturn(Optional.empty());

    mockMvc
        .perform(patch("/api/v1/employers/{id}/status", employerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatusDto)))
        .andExpect(status().isNotFound());
  }
}
