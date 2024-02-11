package ru.mindils.jb.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.util.HibernateUtil;

class MainServiceTest {

  @Test
  void checkUserEntity() {
    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {
      session.beginTransaction();

      User newUser = User.builder()
          .username("mindils")
          .role("ADMIN")
          .password("{noop}pass")
          .modifiedAt(LocalDateTime.now())
          .createdAt(LocalDateTime.now())
          .build();

      session.persist(newUser);
      session.getTransaction().rollback();
    }
  }

  @Test
  void checkVacancyEntity() {
    Vacancy vacancy = Vacancy.builder()
        .id("unique-vacancy-id")
        .name("Разработчик Java")
        .employerId("employer-id-example")
        .premium(false)
        .city("Москва")
        .salary(Salary.builder()
            .salaryFrom(100000)
            .salaryTo(150000)
            .salaryCurrency("RUR")
            .salaryGross(true)
            .build()
        )
        .professionalRoles(List.of(
            Map.of("id", "1", "name", "Разработчик Java"),
            Map.of("id", "2", "name", "Java Developer")
        ))
        .type("Полная занятость")
        .publishedAt(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .archived(false)
        .applyAlternateUrl("http://apply-example.com")
        .url("http://vacancy-example.com")
        .alternateUrl("http://alternate-vacancy-example.com")
        .schedule("Полный день")
        .responseUrl("http://response-example.com")
        .employment("Полная занятость")
        .description("Описание вакансии...")
        .keySkills("Java, Spring")
        .isDetailed(true)
        .localCreatedAt(LocalDateTime.now())
        .localModifiedAt(LocalDateTime.now())
        .build();

    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {
      session.beginTransaction();

      session.persist(vacancy);

      session.getTransaction().rollback();
    }
  }

}