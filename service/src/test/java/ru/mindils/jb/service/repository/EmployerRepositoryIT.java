package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class EmployerRepositoryIT {

  private static SessionFactory sessionFactory;
  private static Session session;

  private EmployerRepository employerRepository;

  @BeforeAll
  static void setUpAll() {
    sessionFactory = HibernateTestUtil.buildSessionFactory();
  }

  @AfterAll
  static void tearDownAll() {
    sessionFactory.close();
  }

  @BeforeEach
  void setUp() {
    session = sessionFactory.openSession();
    session.beginTransaction();
    employerRepository = new EmployerRepository(session);
  }

  @AfterEach
  void tearDown() {
    session.getTransaction().rollback();
    session.close();
  }

  @Test
  public void save() {
    Employer employer = getEmployer();
    employerRepository.save(employer);
    session.flush();

    assertThat(employer.getId()).isNotNull();
  }

  @Test
  public void findById() {
    Employer employer = getEmployer();
    employerRepository.save(employer);
    session.flush();
    session.clear();

    Optional<Employer> actualResult = employerRepository.findById(employer.getId());

    assertThat(actualResult.isPresent()).isTrue();
    assertThat(actualResult.get()).isEqualTo(employer);
  }


  @Test
  public void update() {
    Employer employer = getEmployer();
    employerRepository.save(employer);
    session.flush();

    employer.setName("ООО Рога и копыта 2");
    employerRepository.update(employer);
    session.flush();
    session.clear();

    Optional<Employer> actualResult = employerRepository.findById(employer.getId());

    assertThat(actualResult.isPresent()).isTrue();
    assertThat(actualResult.get()).isEqualTo(employer);
  }

  @Test
  public void delete() {
    Employer employer = getEmployer();
    employerRepository.save(employer);
    session.flush();

    employerRepository.delete(employer);
    session.flush();
    session.clear();

    Optional<Employer> actualResult = employerRepository.findById(employer.getId());

    assertThat(actualResult.isPresent()).isFalse();
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

