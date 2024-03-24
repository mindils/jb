package ru.mindils.jb.integration.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyInfoRepository;
import ru.mindils.jb.service.repository.VacancyRepository;

@RequiredArgsConstructor
public class VacancyInfoRepositoryIT extends ITBase {

    private final VacancyRepository vacancyRepository;
    private final EmployerRepository employerRepository;
    private final VacancyInfoRepository vacancyInfoRepository;
    private final EntityManager entityManager;

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
        vacancyInfoRepository.save(vacancyInfo);
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
