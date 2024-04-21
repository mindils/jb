package ru.mindils.jb.service.http.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mindils.jb.service.dto.EmployerReadDto;
import ru.mindils.jb.service.dto.EmployerUpdateStatusDto;
import ru.mindils.jb.service.service.EmployerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employers")
public class EmployerRestController {

  private final EmployerService employerService;

  @PatchMapping("/{id}/status")
  public EmployerReadDto updateStatus(
      @PathVariable String id, @RequestBody EmployerUpdateStatusDto dto) {
    return employerService
        .updateStatus(id, dto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
