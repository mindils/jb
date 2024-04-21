package ru.mindils.jb.service.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.mindils.jb.service.util.ControllerUtils;

public abstract class PageableBaseController extends BaseController {

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

  @ModelAttribute("currentSort")
  public String currentSort(HttpServletRequest request, Pageable pageable) {
    if (pageable.getSort().isSorted()) {
      return pageable.getSort().stream()
          .map(order ->
              order.getProperty() + "," + order.getDirection().toString().toLowerCase())
          .collect(Collectors.joining(";"));
    }
    return "";
  }
}
