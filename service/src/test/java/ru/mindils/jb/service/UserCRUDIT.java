package ru.mindils.jb.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class UserCRUDIT {

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
  void createUser() {
    User user = getUser();

    session.persist(user);
    session.flush();

    assertThat(user.getId()).isNotNull();
  }

  @Test
  void readUser() {
    User user = getUser();

    session.persist(user);
    session.flush();

    User actualResult = session.get(User.class, user.getId());

    assertThat(actualResult).isEqualTo(user);
  }

  @Test
  void updateUser() {
    User user = getUser();

    session.persist(user);
    session.flush();

    user.setUsername("newUsername");
    session.merge(user);
    session.flush();

    User actualResult = session.get(User.class, user.getId());

    assertThat(actualResult).isEqualTo(user);
  }

  @Test
  void deleteUser() {
    User user = getUser();

    session.persist(user);
    session.flush();

    session.remove(user);
    session.flush();

    User actualResult = session.get(User.class, user.getId());

    assertThat(actualResult).isNull();
  }

  private User getUser() {
    return User.builder()
        .username("mindils")
        .role("ADMIN")
        .password("{noop}pass")
        .modifiedAt(Instant.now())
        .createdAt(Instant.now())
        .build();
  }
}
