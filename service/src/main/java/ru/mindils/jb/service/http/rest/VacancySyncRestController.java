package ru.mindils.jb.service.http.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mindils.jb.service.dto.VacancySyncStatusDto;
import ru.mindils.jb.service.http.controller.BaseController;
import ru.mindils.jb.service.service.VacancySyncService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies-sync")
public class VacancySyncRestController extends BaseController {

  private final VacancySyncService vacancySyncService;

  @GetMapping("/status")
  public VacancySyncStatusDto getStatus() {
    return vacancySyncService.getRunningStatus();
  }
}
