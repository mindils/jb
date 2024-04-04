package ru.mindils.jb.service.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.mindils.jb.service.util.ControllerUtils;

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

  @ModelAttribute("basePaginationUrl")
  public String basePaginationUrl(HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return ControllerUtils.createQueryStringForParams(parameterMap, "page");
  }

  @ModelAttribute("baseSortUrl")
  public String baseSortingUrl(HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    return ControllerUtils.createQueryStringForParams(parameterMap, "page", "sort");
  }
}
