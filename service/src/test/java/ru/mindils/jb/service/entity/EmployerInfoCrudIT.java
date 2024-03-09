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
public class EmployerInfoCrudIT {

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
    void createEmployerInfo() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);

        session.persist(employer);
        session.persist(employerInfo);
        session.flush();

        assertThat(employerInfo.getId()).isNotNull();
    }

    @Test
    void readEmployerInfo() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);

        session.persist(employer);
        session.persist(employerInfo);
        session.flush();
        session.evict(employerInfo);

        EmployerInfo actualResult = session.get(EmployerInfo.class, employerInfo.getId());

        assertThat(actualResult).isEqualTo(employerInfo);
    }

    @Test
    void updateEmployerInfo() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);

        session.persist(employer);
        session.persist(employerInfo);
        session.flush();

        employerInfo.setStatus(EmployerStatusEnum.APPROVED);
        session.merge(employerInfo);
        session.flush();
        session.evict(employerInfo);

        EmployerInfo actualResult = session.get(EmployerInfo.class, employerInfo.getId());

        assertThat(actualResult).isEqualTo(employerInfo);
    }

    @Test
    void deleteEmployerInfo() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);

        session.persist(employer);
        session.persist(employerInfo);
        session.flush();

        session.remove(employerInfo);
        session.flush();
        session.evict(employerInfo);

        EmployerInfo actualResult = session.get(EmployerInfo.class, employerInfo.getId());

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
                .build();
    }

    private static EmployerInfo getEmployerInfo(Employer employer) {
        return EmployerInfo.builder().employer(employer).status(EmployerStatusEnum.NEW).build();
    }
}
