package ru.mindils.jb.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.hibernate.graph.GraphSemantic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mindils.jb.service.config.ApplicationConfiguration;
import ru.mindils.jb.service.config.TestApplicationConfiguration;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.util.TestDataImporter;

public class VacancyEntityGraphsIT {

    private static EntityManager entityManager;

    @BeforeAll
    static void setUpAll() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("test");
        context.register(TestApplicationConfiguration.class, ApplicationConfiguration.class);
        context.refresh();
        entityManager = context.getBean(EntityManager.class);
        TestDataImporter.importData(entityManager);
    }

    @BeforeEach
    void setUp() {
        entityManager.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        entityManager.getTransaction().rollback();
        entityManager.close();
    }

    @Test
    void readVacancyWithEntityGraphs() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);
        VacancyInfo vacancyInfo = getVacancyInfo(vacancy);

        entityManager.persist(employer);
        entityManager.persist(vacancy);
        entityManager.persist(vacancyInfo);
        entityManager.flush();
        entityManager.clear();

        Map<String, Object> properties =
                Map.of(GraphSemantic.FETCH.name(), entityManager.getEntityGraph("Vacancy.detail"));

        Vacancy actualResult = entityManager.find(Vacancy.class, vacancy.getId(), properties);

        assertThat(actualResult).isEqualTo(vacancy);
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
