package ru.mindils.jb.integration.service.http.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
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
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.service.EmployerService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class EmployerControllerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private EmployerService employerService;

  @Test
  void findAll() throws Exception {
    List<Employer> employers =
        Arrays.asList(getEmployer("11111111111"), getEmployer("22222222222"));
    Page<Employer> page = new PageImpl<>(employers);
    when(employerService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc
        .perform(get("/employers"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/employer.list"))
        .andExpect(model().attribute("employers", page))
        .andExpect(model().attribute("currentPage", page.getNumber()))
        .andExpect(model().attribute("totalPages", page.getTotalPages()))
        .andExpect(model().attribute("totalElements", page.getTotalElements()));

    verify(employerService, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void findById() throws Exception {
    String employerId = "1111111111";
    Employer employer = getEmployer(employerId);
    when(employerService.findById(employerId)).thenReturn(employer);

    mockMvc
        .perform(get("/employers/{id}", employerId))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/employer.detail"))
        .andExpect(model().attribute("employer", employer));

    verify(employerService, times(1)).findById(employerId);
  }

  private static Employer getEmployer(String id) {
    return Employer.builder()
        .id(id)
        .name("ООО Рога и копыта")
        .trusted(true)
        .description("Описание работодателя")
        .detailed(true)
        .modifiedAt(Instant.now())
        .createdAt(Instant.now())
        .createdAt(Instant.now())
        .build();
  }
}
