package ru.mindils.jb.service.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

  @GetMapping
  public String home() {
    return "pages/index";
  }
}
