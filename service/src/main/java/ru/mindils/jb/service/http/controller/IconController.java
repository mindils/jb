package ru.mindils.jb.service.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/icons")
public class IconController {

  @GetMapping("/vacancy-status")
  public String getIcon(@RequestParam("status") String status, Model model) {
    model.addAttribute("status", status);
    return "fragments/vacancy-status :: vacancy-status-icon";
  }
}
