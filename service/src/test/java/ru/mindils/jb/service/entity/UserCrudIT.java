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
public class UserCrudIT {

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
        session.evict(user);

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
        session.evict(user);

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
        session.evict(user);

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
