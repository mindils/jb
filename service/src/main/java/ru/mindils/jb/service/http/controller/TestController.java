package ru.mindils.jb.service.http.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.service.VacancyService;
import ru.mindils.jb.service.util.ControllerUtils;
// https://stackoverflow.com/questions/25143756/thymeleaf-modelattribute-list
@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController extends BaseController {

  @GetMapping
  public String testGet(@RequestParam(value = "testDto", required = false) ArrayList<TestDto> testDto) {
    if (testDto == null) {
      testDto = new ArrayList<>();
    }
    return "pages/test";
  }

  @PostMapping
  public String testPost(@ModelAttribute(value = "testDto") ArrayList<TestDto> testDto) {
    if (testDto == null) {
      testDto = new ArrayList<>();
    }
    return "pages/test";
  }


}
