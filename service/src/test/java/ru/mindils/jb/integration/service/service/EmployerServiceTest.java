package ru.mindils.jb.integration.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.EmployerReadDto;
import ru.mindils.jb.service.dto.EmployerUpdateStatusDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerStatusEnum;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.service.EmployerService;

@RequiredArgsConstructor
public class EmployerServiceTest extends ITBase {

  private final EmployerService employerService;
  private final EmployerRepository employerRepository;

  @Test
  void findAll() {
    Page<Employer> result = employerService.findAll(PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(5);
  }

  @Test
  void findById() {
    Employer employer = new Employer();
    employer.setId("1");
    employer.setName("Employer 1");

    employerRepository.save(employer);

    Employer result = employerService.findById("1");

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
    assertThat(result.getName()).isEqualTo("Employer 1");
  }

  @Test
  void updateStatus() {
    employerRepository.save(getEmployer("update1"));

    EmployerUpdateStatusDto updateStatusDto =
        new EmployerUpdateStatusDto(EmployerStatusEnum.APPROVED);

    Optional<EmployerReadDto> result = employerService.updateStatus("update1", updateStatusDto);

    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo("update1");
    assertThat(result.get().name()).isEqualTo("Employer update1");

    Employer updatedEmployer = employerRepository.findById("update1").orElseThrow();
    assertThat(updatedEmployer.getEmployerInfo()).isNotNull();
    assertThat(updatedEmployer.getEmployerInfo().getStatus())
        .isEqualTo(EmployerStatusEnum.APPROVED);
  }

  private static Employer getEmployer(String id) {
    return Employer.builder()
        .id(id)
        .name("Employer " + id)
        .trusted(true)
        .description("Описание работодателя")
        .detailed(true)
        .modifiedAt(Instant.now())
        .createdAt(Instant.now())
        .createdAt(Instant.now())
        .build();
  }
}
