package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

public class VacancyInfoRepositoryIT extends BaseRepositoryIT {

    private static VacancyRepository vacancyRepository;
    private static EmployerRepository employerRepository;
    private static VacancyInfoRepository vacancyInfoRepository;

    @BeforeAll
    static void setUpAll() {
        init();

        vacancyRepository = context.getBean(VacancyRepository.class);
        employerRepository = context.getBean(EmployerRepository.class);
        vacancyInfoRepository = context.getBean(VacancyInfoRepository.class);
    }

    @AfterEach
    void tearDown() {
        entityManager.getTransaction().rollback();
        entityManager.close();
    }

    @Test
    void save() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);
        VacancyInfo vacancyInfo = getVacancyInfo(vacancy);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        vacancyInfoRepository.save(vacancyInfo);

        assertThat(vacancyInfo.getId()).isNotNull();
    }

    @Test
    void findById() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);
        VacancyInfo vacancyInfo = getVacancyInfo(vacancy);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        vacancyInfoRepository.save(vacancyInfo);

        entityManager.flush();
        entityManager.clear();

        Optional<VacancyInfo> actualResult = vacancyInfoRepository.findById(vacancyInfo.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyInfo);
    }

    @Test
    void update() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);
        VacancyInfo vacancyInfo = getVacancyInfo(vacancy);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        vacancyInfoRepository.save(vacancyInfo);
        entityManager.flush();

        vacancyInfo.setAiApproved(BigDecimal.valueOf(0.7777));
        vacancyInfoRepository.update(vacancyInfo);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyInfo> actualResult = vacancyInfoRepository.findById(vacancyInfo.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyInfo);
    }

    @Test
    void delete() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);
        VacancyInfo vacancyInfo = getVacancyInfo(vacancy);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        vacancyInfoRepository.save(vacancyInfo);
        entityManager.flush();

        vacancyInfoRepository.delete(vacancyInfo);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyInfo> actualResult = vacancyInfoRepository.findById(vacancyInfo.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static VacancyInfo getVacancyInfo(Vacancy vacancy) {
        return VacancyInfo.builder()
                .vacancy(vacancy)
                .aiApproved(BigDecimal.valueOf(0.7532))
                .status(VacancyStatusEnum.NEW)
                .build();
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
