package ru.mindils.jb.service.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.mindils.jb.service.util.HibernateTestUtil;

@TestInstance(PER_CLASS)
public class VacancyCrudIT {

    private SessionFactory sessionFactory;
    private Session session;

    @BeforeAll
    void setUpAll() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
    }

    @AfterAll
    void tearDownAll() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    void createVacancy() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        session.persist(employer);
        session.persist(vacancy);

        assertThat(vacancy.getId()).isNotNull();
    }

    @Test
    void readVacancy() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        session.persist(employer);
        session.persist(vacancy);
        session.flush();

        session.evict(employer);
        session.evict(vacancy);

        Vacancy actualResult = session.get(Vacancy.class, vacancy.getId());

        assertThat(actualResult).isEqualTo(vacancy);
    }

    @Test
    void updateVacancy() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        session.persist(employer);
        session.persist(vacancy);

        Vacancy readVacancy = session.get(Vacancy.class, vacancy.getId());
        readVacancy.setName("Java Developer");

        session.merge(readVacancy);
        session.flush();
        session.evict(vacancy);

        Vacancy actualResult = session.get(Vacancy.class, vacancy.getId());

        assertThat(actualResult).isEqualTo(readVacancy);
    }

    @Test
    void deleteVacancy() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        session.persist(employer);
        session.persist(vacancy);

        Vacancy readVacancy = session.get(Vacancy.class, vacancy.getId());

        session.remove(readVacancy);
        session.flush();
        session.evict(vacancy);

        Vacancy actualResult = session.get(Vacancy.class, vacancy.getId());

        assertThat(actualResult).isNull();
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
