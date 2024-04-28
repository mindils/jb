package ru.mindils.jb.service.http.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.service.UserService;

@Controller
@RequestMapping("/settings")
public class SettingsController extends BaseController {

  private final UserService userService;

  public SettingsController(UserService userService) {
    super();
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public String settings(Model model, Principal principal) {
    model.addAttribute("users", userService.findAll());
    model.addAttribute("roles", List.of("USER", "ADMIN"));
    return "pages/settings";
  }
}
