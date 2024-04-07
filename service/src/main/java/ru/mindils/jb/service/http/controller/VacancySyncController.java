package ru.mindils.jb.service.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mindils.jb.service.service.VacancySyncService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vacancies-sync")
public class VacancySyncController extends BaseController {

  private final VacancySyncService vacancySyncService;

  @GetMapping
  public String syncAll(Model model) {
    model.addAttribute("isSyncRunning", vacancySyncService.isSyncRunning());

    return "pages/vacancy-sync.all";
  }

  @PostMapping
  public String syncAllPost(@RequestParam String syncPeriod) {
    vacancySyncService.startAllSync(syncPeriod);
    return "redirect:/vacancies-sync";
  }
}
