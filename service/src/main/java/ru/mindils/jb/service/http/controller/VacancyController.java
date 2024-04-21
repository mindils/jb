package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.service.VacancyService;
import ru.mindils.jb.service.service.VacancySyncService;
import ru.mindils.jb.sync.entity.ProgressType;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController extends PageableBaseController {

  private final VacancyService vacancyService;
  private final VacancySyncService vacancySyncService;

  @GetMapping
  public String findAll(Model model, AppVacancyFilterDto vacancyFilter, Pageable pageable) {
    Page<VacancyReadDto> vacancies = vacancyService.findAll(vacancyFilter, pageable);
    model.addAttribute("vacancies", vacancies);
    model.addAttribute("filter", vacancyFilter);
    model.addAttribute("currentPage", vacancies.getNumber());
    model.addAttribute("totalPages", vacancies.getTotalPages());
    model.addAttribute("totalElements", vacancies.getTotalElements());

    return "pages/vacancy.list";
  }

  @PostMapping
  public String updateVacancy(AppVacancyFilterDto vacancyFilter) {
    vacancyService.setDetailedFalse(vacancyFilter);
    vacancySyncService.startVacancySync(ProgressType.ONE_STEP);

    return "redirect:/vacancies";
  }

  @GetMapping("/{id}")
  public String findById(Model model, @PathVariable String id) {
    model.addAttribute("vacancy", vacancyService.findById(id));
    return "pages/vacancy.detail";
  }
}
