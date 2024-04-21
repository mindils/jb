package ru.mindils.jb.integration.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.service.service.VacancyService;

@RequiredArgsConstructor
public class VacancyServiceTest extends ITBase {

  private final VacancyService vacancyService;

  private final VacancyRepository vacancyRepository;
  private final EmployerRepository employerRepository;

  @Test
  void findAll() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().build();

    Page<VacancyReadDto> result = vacancyService.findAll(filter, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(4);
  }

  @Test
  void setDetailedFalse() {
    Employer employer = getEmployer("employer-1");
    Vacancy vacancy1 = getVacancy("vacancy-1", employer);
    Vacancy vacancy2 = getVacancy("vacancy-2", employer);

    employerRepository.save(employer);
    vacancyRepository.saveAll(List.of(vacancy1, vacancy2));

    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().build();

    vacancyService.setDetailedFalse(filter);

    List<Vacancy> updatedVacancies = vacancyRepository.findAll();
    assertThat(updatedVacancies).extracting(Vacancy::getDetailed).containsOnly(false);
  }

  @Test
  void findById() {
    Employer employer = getEmployer("1");
    Vacancy vacancy = getVacancy("1", employer);

    employerRepository.save(employer);
    vacancyRepository.save(vacancy);

    VacancyReadDto result = vacancyService.findById("1");

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo("1");
    assertThat(result.name()).isEqualTo("Разработчик Java");
  }

  private static Employer getEmployer(String id) {
    return Employer.builder()
        .id(id)
        .name("ООО Рога и копыта")
        .trusted(true)
        .description("Описание работодателя")
        .detailed(true)
        .createdAt(Instant.now())
        .build();
  }

  private static VacancyInfo getVacancyInfo(Vacancy vacancy) {
    return VacancyInfo.builder()
        .vacancy(vacancy)
        .aiApproved(BigDecimal.valueOf(0.7532))
        .status(VacancyStatusEnum.NEW)
        .build();
  }

  private static Vacancy getVacancy(String id, Employer employer) {
    return Vacancy.builder()
        .id(id)
        .name("Разработчик Java")
        .employer(employer)
        .premium(false)
        .city("Москва")
        .salary(
            Salary.builder().from(100000).to(150000).currency("RUR").gross(true).build())
        .type("open")
        .publishedAt(Instant.now())
        .createdAt(Instant.now())
        .archived(false)
        .applyAlternateUrl("https://example.com")
        .url("https://example.com")
        .alternateUrl("https://example.com")
        .schedule("fullDay")
        .responseUrl("https://example.com")
        .professionalRoles(List.of(Map.of("id", "1", "name", "Java Developer")))
        .employment("full")
        .description("Описание вакансии")
        .build();
  }
}
