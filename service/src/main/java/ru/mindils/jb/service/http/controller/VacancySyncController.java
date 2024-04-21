package ru.mindils.jb.service.http.controller;

import java.time.LocalDate;
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

  @PostMapping("/employer")
  public String syncEmployer(@RequestParam LocalDate syncDate) {
    vacancySyncService.startEmployerSync(syncDate);
    return "redirect:/vacancies-sync";
  }

  @PostMapping("/vacancy")
  public String syncVacancy(@RequestParam LocalDate syncDate) {
    vacancySyncService.startVacancySync(syncDate);
    return "redirect:/vacancies-sync";
  }

  @PostMapping("/vacancy-ai")
  public String syncVacancyAi() {
    vacancySyncService.startVacancyAiSync();
    return "redirect:/vacancies-sync";
  }
}
