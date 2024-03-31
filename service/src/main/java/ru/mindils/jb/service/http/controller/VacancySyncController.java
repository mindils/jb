package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vacancies-sync")
public class VacancySyncController extends BaseController {

  @GetMapping
  public String syncAll() {
    return "pages/vacancy-sync.all";
  }
}
