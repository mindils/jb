package ru.mindils.jb.integration.service.http.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.service.VacancySyncService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class VacancySyncControllerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private VacancySyncService vacancySyncService;

  @Test
  public void syncAll() throws Exception {
    when(vacancySyncService.isSyncRunning()).thenReturn(true);

    mockMvc
        .perform(get("/vacancies-sync"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/vacancy-sync.all"))
        .andExpect(model().attribute("isSyncRunning", true));

    verify(vacancySyncService, times(1)).isSyncRunning();
  }

  @Test
  public void syncAllPost() throws Exception {
    String syncPeriod = "30";

    mockMvc
        .perform(post("/vacancies-sync").with(csrf()).param("syncPeriod", syncPeriod))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/vacancies-sync"));

    verify(vacancySyncService, times(1)).startAllSync(syncPeriod);
  }

  @Test
  public void syncEmployer() throws Exception {
    LocalDate syncDate = LocalDate.of(2023, 6, 1);

    mockMvc
        .perform(
            post("/vacancies-sync/employer").with(csrf()).param("syncDate", syncDate.toString()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/vacancies-sync"));

    verify(vacancySyncService, times(1)).startEmployerSync(syncDate);
  }

  @Test
  public void syncVacancy() throws Exception {
    LocalDate syncDate = LocalDate.of(2023, 6, 1);

    mockMvc
        .perform(
            post("/vacancies-sync/vacancy").with(csrf()).param("syncDate", syncDate.toString()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/vacancies-sync"));

    verify(vacancySyncService, times(1)).startVacancySync(syncDate);
  }

  @Test
  public void syncVacancyAi() throws Exception {
    mockMvc
        .perform(post("/vacancies-sync/vacancy-ai").with(csrf()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/vacancies-sync"));

    verify(vacancySyncService, times(1)).startVacancyAiSync();
  }
}
