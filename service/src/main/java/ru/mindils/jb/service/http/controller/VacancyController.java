package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.service.VacancyService;
import ru.mindils.jb.service.util.ControllerUtils;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController extends BaseController {

  private final VacancyService vacancyService;

  @ModelAttribute("salaryFromOrder")
  public String salaryFromSort(Pageable pageable) {
    Order orderFor = pageable.getSort().getOrderFor("salary.from");
    return ControllerUtils.toggleSortDirection(orderFor);
  }

  @ModelAttribute("createdAtOrder")
  public String createdAtOrder(Pageable pageable) {
    return ControllerUtils.toggleSortDirection(pageable.getSort().getOrderFor("createdAt"));
  }

  @GetMapping
  public String findAll(Model model, AppVacancyFilterDto vacancyFilter, Pageable pageable) {
    Page<Vacancy> vacancies = vacancyService.findAll(vacancyFilter, pageable);
    model.addAttribute("vacancies", vacancies);
    model.addAttribute("filter", vacancyFilter);
    model.addAttribute("currentPage", vacancies.getNumber());
    model.addAttribute("totalPages", vacancies.getTotalPages());

    return "pages/vacancy.list";
  }

  @GetMapping("/{id}")
  public String findById(Model model, @PathVariable String id) {
    model.addAttribute("vacancy", vacancyService.findById(id));
    return "pages/vacancy.detail";
  }
}
