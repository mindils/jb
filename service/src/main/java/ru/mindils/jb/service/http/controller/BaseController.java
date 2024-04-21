package ru.mindils.jb.service.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

  @ModelAttribute("activePage")
  public String activePage(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String[] segments = requestURI.split("/");
    if (segments.length > 1) {
      return segments[1];
    }
    return "home";
  }

  @ModelAttribute("currentLang")
  public String language() {
    return LocaleContextHolder.getLocale().getLanguage();
  }
}
