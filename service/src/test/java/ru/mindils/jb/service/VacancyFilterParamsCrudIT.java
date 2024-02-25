package ru.mindils.jb.service;

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
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.entity.VacancyFilterParams;
import ru.mindils.jb.service.util.HibernateTestUtil;

@TestInstance(PER_CLASS)
public class VacancyFilterParamsCrudIT {

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
  void createVacancyFilterParams() {
    VacancyFilter vacancyFilter = getVacancyFilter();
    VacancyFilterParams vacancyFilterParams = getVacancyFilterParams(vacancyFilter);

    session.persist(vacancyFilter);
    session.persist(vacancyFilterParams);

    assertThat(vacancyFilterParams.getId()).isNotNull();
  }

  @Test
  void readVacancyFilterParams() {
    VacancyFilter vacancyFilter = getVacancyFilter();
    VacancyFilterParams vacancyFilterParams = getVacancyFilterParams(vacancyFilter);

    session.persist(vacancyFilter);
    session.persist(vacancyFilterParams);
    session.evict(vacancyFilterParams);

    VacancyFilterParams actualResult = session.get(VacancyFilterParams.class,
        vacancyFilterParams.getId());

    assertThat(actualResult).isEqualTo(vacancyFilterParams);
  }

  @Test
  void updateVacancyFilterParams() {
    VacancyFilter vacancyFilter = getVacancyFilter();
    VacancyFilterParams vacancyFilterParams = getVacancyFilterParams(vacancyFilter);

    session.persist(vacancyFilter);
    session.persist(vacancyFilterParams);
    session.flush();
    session.evict(vacancyFilterParams);

    vacancyFilterParams.setParamName("newParamName");
    session.merge(vacancyFilterParams);
    session.flush();

    VacancyFilterParams actualResult = session.get(VacancyFilterParams.class,
        vacancyFilterParams.getId());

    assertThat(actualResult).isEqualTo(vacancyFilterParams);
  }

  @Test
  void deleteVacancyFilterParams() {
    VacancyFilter vacancyFilter = getVacancyFilter();
    VacancyFilterParams vacancyFilterParams = getVacancyFilterParams(vacancyFilter);

    session.persist(vacancyFilter);
    session.persist(vacancyFilterParams);
    session.flush();

    session.remove(vacancyFilterParams);
    session.flush();
    session.evict(vacancyFilterParams);

    VacancyFilterParams actualResult = session.get(VacancyFilterParams.class,
        vacancyFilterParams.getId());

    assertThat(actualResult).isNull();
  }

  private static VacancyFilter getVacancyFilter() {
    return VacancyFilter.builder()
        .code("default")
        .name("Default Filter")
        .modifiedAt(Instant.now())
        .createdAt(Instant.now())
        .build();

  }

  private static VacancyFilterParams getVacancyFilterParams(VacancyFilter vacancyFilter) {
    return VacancyFilterParams.builder()
        .vacancyFilter(vacancyFilter)
        .paramName("city")
        .paramValue("1")
        .build();
  }
}