package ru.mindils.jb.integration.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerInfo;
import ru.mindils.jb.service.entity.EmployerStatusEnum;
import ru.mindils.jb.service.repository.EmployerInfoRepository;
import ru.mindils.jb.service.repository.EmployerRepository;

@RequiredArgsConstructor
public class EmployerInfoRepositoryIT extends ITBase {

  private final EntityManager entityManager;
  private final EmployerInfoRepository employerInfoRepository;
  private final EmployerRepository employerRepository;

  @Test
  void save() {
    Employer employer = getEmployer();
    EmployerInfo employerInfo = getEmployerInfo(employer);
    employerRepository.save(employer);
    employerInfoRepository.save(employerInfo);
    entityManager.flush();

    assertThat(employerInfo.getId()).isNotNull();
  }

  @Test
  void findById() {
    Employer employer = getEmployer();
    EmployerInfo employerInfo = getEmployerInfo(employer);
    employerRepository.save(employer);
    employerInfoRepository.save(employerInfo);
    entityManager.flush();
    entityManager.clear();

    Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());

    assertThat(actualResult.isPresent()).isTrue();
    assertThat(actualResult.get()).isEqualTo(employerInfo);
  }

  @Test
  void update() {
    Employer employer = getEmployer();
    EmployerInfo employerInfo = getEmployerInfo(employer);
    employerRepository.save(employer);
    employerInfoRepository.save(employerInfo);
    entityManager.flush();

    employerInfo.setStatus(EmployerStatusEnum.APPROVED);
    employerInfoRepository.save(employerInfo);
    entityManager.flush();
    entityManager.clear();

    Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());
    actualResult.ifPresent(e -> assertThat(e).isEqualTo(employerInfo));
  }

  @Test
  void delete() {
    Employer employer = getEmployer();
    EmployerInfo employerInfo = getEmployerInfo(employer);
    employerRepository.save(employer);
    employerInfoRepository.save(employerInfo);
    entityManager.flush();

    employerInfoRepository.delete(employerInfo);
    entityManager.flush();
    entityManager.clear();

    Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());
    assertThat(actualResult.isPresent()).isFalse();
  }

  private static Employer getEmployer() {
    return Employer.builder()
        .id("employer-id-example")
        .name("ООО Рога и копыта")
        .trusted(true)
        .description("Описание работодателя")
        .detailed(true)
        .modifiedAt(Instant.now())
        .createdAt(Instant.now())
        .build();
  }

  private static EmployerInfo getEmployerInfo(Employer employer) {
    return EmployerInfo.builder()
        .employer(employer)
        .status(EmployerStatusEnum.NEW)
        .build();
  }
}
