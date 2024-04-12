package ru.mindils.jb.service.http.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/profile")
  @PreAuthorize("hasAnyAuthority('USER')")
  public String profile(Principal principal, Model model) {
    model.addAttribute("user", userService.findByUsername(principal.getName()));
    return "pages/user.profile";
  }

  @PostMapping("/profile")
  @PreAuthorize("hasAnyAuthority('USER')")
  public String updateProfile(Principal principal, UserUpdateDto user) {
    userService.updateProfile(principal.getName(), user);
    return "redirect:/user/profile";
  }
}
