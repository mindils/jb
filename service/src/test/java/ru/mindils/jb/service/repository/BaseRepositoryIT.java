package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mindils.jb.service.config.ApplicationConfiguration;
import ru.mindils.jb.service.config.TestApplicationConfiguration;

public abstract class BaseRepositoryIT {

    protected static SessionFactory sessionFactory;
    protected static EntityManager entityManager;
    protected static AnnotationConfigApplicationContext context;

    static void init() {
        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("test");
        context.register(TestApplicationConfiguration.class, ApplicationConfiguration.class);
        context.refresh();
    }

    @BeforeEach
    void setUp() {
        entityManager = context.getBean(EntityManager.class);
        entityManager.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        entityManager.getTransaction().rollback();
        entityManager.close();
    }
}
