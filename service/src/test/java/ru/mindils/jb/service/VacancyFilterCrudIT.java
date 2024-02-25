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
public class VacancyFilterCrudIT {

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
  void createVacancyFilter() {
    VacancyFilter vacancyFilter = getVacancyFilter();

    session.persist(vacancyFilter);

    assertThat(vacancyFilter.getId()).isNotNull();
  }

  @Test
  void readVacancyFilter() {
    VacancyFilter vacancyFilter = getVacancyFilter();

    session.persist(vacancyFilter);
    session.evict(vacancyFilter);

    VacancyFilter actualResult = session.get(VacancyFilter.class, vacancyFilter.getId());

    assertThat(actualResult).isEqualTo(vacancyFilter);
  }

  @Test
  void updateVacancyFilter() {
    VacancyFilter vacancyFilter = getVacancyFilter();

    session.persist(vacancyFilter);
    session.flush();

    vacancyFilter.setName("Новое название фильтра");
    session.merge(vacancyFilter);
    session.flush();
    session.evict(vacancyFilter);

    VacancyFilter actualResult = session.get(VacancyFilter.class, vacancyFilter.getId());

    assertThat(actualResult).isEqualTo(vacancyFilter);
  }

  @Test
  void deleteVacancyFilterTest() {
    VacancyFilter vacancyFilter = getVacancyFilter();

    session.persist(vacancyFilter);
    session.flush();

    session.remove(vacancyFilter);
    session.flush();
    session.evict(vacancyFilter);

    VacancyFilter actualResult = session.get(VacancyFilter.class, vacancyFilter.getId());

    assertThat(actualResult).isNull();
  }

  private static VacancyFilter getVacancyFilter() {
    VacancyFilter vacancyFilter = VacancyFilter.builder()
        .code("default")
        .name("Фильтр по умолчанию")
        .createdAt(Instant.now())
        .modifiedAt(Instant.now())
        .build();

    vacancyFilter.addParam(
        VacancyFilterParams.builder()
            .paramName("city")
            .paramValue("1")
            .build()
    );

    vacancyFilter.addParam(
        VacancyFilterParams.builder()
            .paramName("text")
            .paramValue("java")
            .build()
    );

    return vacancyFilter;
  }


}
