package ru.mindils.jb.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerInfo;
import ru.mindils.jb.service.entity.EmployerStatusEnum;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class EmployerInfoCRUDIT {

  private SessionFactory sessionFactory;
  private Session session;

  @BeforeEach
  void setUp() {
    sessionFactory = HibernateTestUtil.buildSessionFactory();
    session = sessionFactory.openSession();
    session.beginTransaction();
  }

  @AfterEach
  void tearDown() {
    session.getTransaction().rollback();
    session.close();
    sessionFactory.close();
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

    EmployerInfo actualResult = session.get(EmployerInfo.class,
        employerInfo.getId());

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

    EmployerInfo actualResult = session.get(EmployerInfo.class,
        employerInfo.getId());

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

    EmployerInfo actualResult = session.get(EmployerInfo.class,
        employerInfo.getId());

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
    return EmployerInfo.builder()
        .employer(employer)
        .status(EmployerStatusEnum.NEW)
        .build();
  }
}
