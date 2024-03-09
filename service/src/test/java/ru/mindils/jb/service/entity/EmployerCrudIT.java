package ru.mindils.jb.service.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.time.Instant;
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
public class EmployerCrudIT {

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
    void createEmployer() {
        Employer employer = getEmployer();

        session.persist(employer);
        session.flush();

        assertThat(employer.getId()).isNotNull();
    }

    @Test
    void readEmployer() {
        Employer employer = getEmployer();

        session.persist(employer);
        session.flush();
        session.evict(employer);

        Employer actualResult = session.get(Employer.class, employer.getId());

        assertThat(actualResult).isEqualTo(employer);
    }

    @Test
    void updateEmployer() {
        Employer employer = getEmployer();

        session.persist(employer);
        session.flush();

        employer.setName("ООО Рога и копыта 2");
        session.merge(employer);
        session.flush();
        session.evict(employer);

        Employer actualResult = session.get(Employer.class, employer.getId());

        assertThat(actualResult).isEqualTo(employer);
    }

    @Test
    void deleteEmployer() {
        Employer employer = getEmployer();

        session.persist(employer);
        session.flush();

        session.remove(employer);
        session.flush();
        session.evict(employer);

        Employer actualResult = session.get(Employer.class, employer.getId());

        assertThat(actualResult).isNull();
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
                .createdAt(Instant.now())
                .build();
    }
}
