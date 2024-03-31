package ru.mindils.jb.service.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsController extends BaseController {

  @GetMapping
  public String settings() {
    return "pages/settings";
  }
}
