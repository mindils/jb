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
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class UserRepositoryIT {

  private static SessionFactory sessionFactory;
  private static Session session;

  private UserRepository userRepository;

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
    userRepository = new UserRepository(session);
  }

  @AfterEach
  void tearDown() {
    session.getTransaction().rollback();
    session.close();
  }

  @Test
  public void save() {
    User user = getUser();
    userRepository.save(user);

    assertThat(user.getId()).isNotNull();
  }

  @Test
  public void findById() {
    User user = getUser();
    userRepository.save(user);
    session.flush();
    session.clear();

    Optional<User> actualResult = userRepository.findById(user.getId());

    assertThat(actualResult.isPresent()).isTrue();
    assertThat(actualResult.get()).isEqualTo(user);
  }


  @Test
  public void update() {
    User user = getUser();
    userRepository.save(user);
    session.flush();

    user.setUsername("newUsername");
    userRepository.update(user);
    session.flush();
    session.clear();

    Optional<User> actualResult = userRepository.findById(user.getId());

    assertThat(actualResult.isPresent()).isTrue();
    assertThat(actualResult.get()).isEqualTo(user);
  }

  @Test
  public void delete() {
    User user = getUser();
    userRepository.save(user);
    session.flush();

    userRepository.delete(user);
    session.flush();
    session.clear();

    Optional<User> actualResult = userRepository.findById(user.getId());
    assertThat(actualResult.isPresent()).isFalse();
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
