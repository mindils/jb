package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.service.EmployerService;

@Controller
@RequestMapping("/employers")
@RequiredArgsConstructor
public class EmployerController extends BaseController {

  private final EmployerService employerService;

  @GetMapping
  public String findAll(Model model, Pageable pageable) {
    Page<Employer> employers = employerService.findAll(pageable);
    model.addAttribute("employers", employers);
    model.addAttribute("currentPage", employers.getNumber());
    model.addAttribute("totalPages", employers.getTotalPages());
    return "pages/employer.list";
  }

  @GetMapping("/{id}")
  public String findById(Model model, @PathVariable String id) {
    model.addAttribute("employer", employerService.findById(id));
    return "pages/employer.detail";
  }
}
