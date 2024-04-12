package ru.mindils.jb.service.http.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.service.VacancyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
public class VacancyRestController {
  private final VacancyService vacancyService;

  @PatchMapping("/{id}")
  public UpdateStatusVacancyDto updateStatus(
      @PathVariable String id, @RequestBody UpdateStatusVacancyDto dto) {
    return vacancyService
        .updateStatus(id, dto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
