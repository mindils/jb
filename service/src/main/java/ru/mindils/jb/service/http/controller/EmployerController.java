package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.service.EmployerService;

@Controller
@RequestMapping("/employers")
@RequiredArgsConstructor
public class EmployerController extends BaseController {

  private final EmployerService employerService;

  @GetMapping
  public String findAll() {
    return "pages/employer.list";
  }

  @GetMapping("/{id}")
  public String findById(Model model, @PathVariable String id) {
    model.addAttribute("employer", employerService.findById(id));
    return "pages/employer.detail";
  }
}
