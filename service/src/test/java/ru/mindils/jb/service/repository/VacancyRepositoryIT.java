package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.util.TestDataImporter;

public class VacancyRepositoryIT extends BaseRepositoryIT {

    private static VacancyRepository vacancyRepository;
    private static EmployerRepository employerRepository;

    @BeforeAll
    static void setUpAll() {
        init();
        var entityManager = context.getBean(EntityManager.class);
        TestDataImporter.importData(entityManager);
        vacancyRepository = context.getBean(VacancyRepository.class);
        employerRepository = context.getBean(EmployerRepository.class);
    }

    @Test
    void findByFilter() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .aiApproved(BigDecimal.valueOf(0.7))
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(100000)
                        .salaryTo(300000)
                        .build();

        List<Vacancy> actualResult = vacancyRepository.findByFilter(filter);

        assertThat(actualResult).hasSize(1);
    }

    @Test
    public void save() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);

        assertThat(vacancy.getId()).isNotNull();
    }

    @Test
    void findById() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        entityManager.flush();
        entityManager.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(vacancy);
    }

    @Test
    void update() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        entityManager.flush();

        vacancy.setName("new Vacancy");
        vacancyRepository.update(vacancy);
        entityManager.flush();
        entityManager.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancy);
    }

    @Test
    void delete() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        entityManager.flush();

        vacancyRepository.delete(vacancy);
        entityManager.flush();
        entityManager.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static Employer getEmployer() {
        return Employer.builder()
                .id("employer-id-example")
                .name("ООО Рога и копыта")
                .trusted(true)
                .description("Описание работодателя")
                .detailed(true)
                .createdAt(Instant.now())
                .build();
    }

    private static Vacancy getVacancy(Employer employer) {
        return Vacancy.builder()
                .id("unique-vacancy-id")
                .name("Разработчик Java")
                .employer(employer)
                .premium(false)
                .city("Москва")
                .salary(
                        Salary.builder()
                                .from(100000)
                                .to(150000)
                                .currency("RUR")
                                .gross(true)
                                .build())
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
