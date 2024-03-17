package ru.mindils.jb.integration.sync.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.repository.EmployerRepository;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.sync.service.SyncVacancyAIService;

@RequiredArgsConstructor
@Transactional
public class SyncVacancyAIServiceIT extends ITBase {

    private final SyncVacancyAIService syncVacancyAIService;
    private final EmployerRepository employerRepository;
    private final VacancyRepository vacancyRepository;
    private final EntityManager entityManager;

    @Test
    void testSyncVacancyAiRating() {

        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        entityManager.flush();

        syncVacancyAIService.syncVacancyAiRating(vacancy);
        entityManager.flush();

        VacancyInfo vacancyInfo = vacancy.getVacancyInfo();

        assertThat(vacancyInfo).isNotNull();

        assertThat(vacancyInfo.getAiApproved()).isNotNull();
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
