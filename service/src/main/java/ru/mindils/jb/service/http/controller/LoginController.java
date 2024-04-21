package ru.mindils.jb.service.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/login")
@Controller
public class LoginController extends BaseController {

  @GetMapping
  public String login() {
    return "pages/login";
  }
}
