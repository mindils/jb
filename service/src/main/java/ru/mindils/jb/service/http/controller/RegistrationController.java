package ru.mindils.jb.service.http.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.service.UserService;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

  private final UserService userService;

  @GetMapping
  public String showRegistration(RegistrationDto registrationDto, Model model) {
    model.addAttribute("registrationDto", registrationDto);
    return "pages/registration";
  }

  @PostMapping
  public String registration(
      @Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
      BindingResult bindingResult,
      Model model) {
    model.addAttribute("registrationDto", registrationDto);
    if (bindingResult.hasErrors()) {
      return "pages/registration";
    }
    userService.create(registrationDto);

    return "redirect:/login?registration";
  }
}
