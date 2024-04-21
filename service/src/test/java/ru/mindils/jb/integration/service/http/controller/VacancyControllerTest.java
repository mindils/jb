package ru.mindils.jb.integration.service.http.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.service.VacancyService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class VacancyControllerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private VacancyService vacancyService;

  @Test
  void findAll() throws Exception {
    AppVacancyFilterDto filterDto = AppVacancyFilterDto.builder().build();
    List<VacancyReadDto> vacancies = Arrays.asList(getVacancy("1"), getVacancy("2"));
    Page<VacancyReadDto> page = new PageImpl<>(vacancies);
    when(vacancyService.findAll(any(AppVacancyFilterDto.class), any(Pageable.class)))
        .thenReturn(page);

    mockMvc
        .perform(get("/vacancies"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/vacancy.list"))
        .andExpect(model().attribute("vacancies", page))
        .andExpect(model().attribute("filter", filterDto))
        .andExpect(model().attribute("currentPage", page.getNumber()))
        .andExpect(model().attribute("totalPages", page.getTotalPages()))
        .andExpect(model().attribute("totalElements", page.getTotalElements()));

    verify(vacancyService, times(1)).findAll(any(AppVacancyFilterDto.class), any(Pageable.class));
  }

  @Test
  void findById() throws Exception {
    String vacancyId = "1";
    VacancyReadDto vacancy = getVacancy(vacancyId);
    when(vacancyService.findById(vacancyId)).thenReturn(vacancy);

    mockMvc
        .perform(get("/vacancies/{id}", vacancyId))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/vacancy.detail"))
        .andExpect(model().attribute("vacancy", vacancy));

    verify(vacancyService, times(1)).findById(vacancyId);
  }

  private VacancyReadDto getVacancy(String id) {
    return new VacancyReadDto(
        id,
        "Vacancy" + id,
        null,
        false,
        "City " + id,
        null,
        "Type " + id,
        null,
        null,
        false,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        null,
        null);
  }
}
