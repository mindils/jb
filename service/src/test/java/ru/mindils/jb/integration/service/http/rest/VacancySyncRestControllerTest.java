package ru.mindils.jb.integration.service.http.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.VacancySyncStatusDto;
import ru.mindils.jb.service.service.VacancySyncService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class VacancySyncRestControllerTest extends ITBase {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean
  private VacancySyncService vacancySyncService;

  @Test
  void getStatus_Success() throws Exception {
    VacancySyncStatusDto vacancySyncStatusDto = VacancySyncStatusDto.builder()
        .syncRunning(true)
        .statusText("Sync in progress")
        .progress(50)
        .step(1)
        .build();
    when(vacancySyncService.getRunningStatus()).thenReturn(vacancySyncStatusDto);

    String response = mockMvc
        .perform(get("/api/v1/vacancies-sync/status"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    VacancySyncStatusDto actualVacancySyncStatusDto =
        objectMapper.readValue(response, VacancySyncStatusDto.class);
    assertThat(actualVacancySyncStatusDto).isEqualTo(vacancySyncStatusDto);
  }

  @Test
  void getStatus_NoSyncRunning() throws Exception {
    VacancySyncStatusDto vacancySyncStatusDto = VacancySyncStatusDto.builder()
        .syncRunning(false)
        .statusText("No sync running")
        .progress(100)
        .step(0)
        .build();
    when(vacancySyncService.getRunningStatus()).thenReturn(vacancySyncStatusDto);

    String response = mockMvc
        .perform(get("/api/v1/vacancies-sync/status"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    VacancySyncStatusDto actualVacancySyncStatusDto =
        objectMapper.readValue(response, VacancySyncStatusDto.class);
    assertThat(actualVacancySyncStatusDto).isEqualTo(vacancySyncStatusDto);
  }
}
