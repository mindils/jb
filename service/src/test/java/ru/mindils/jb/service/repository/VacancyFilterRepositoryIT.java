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
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.entity.VacancyFilterParams;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class VacancyFilterRepositoryIT {

    private static SessionFactory sessionFactory;
    private static Session session;

    private VacancyFilterRepository vacancyFilterRepository;

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
        vacancyFilterRepository = new VacancyFilterRepository(session);
    }

    @AfterEach
    void tearDown() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void save() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);

        assertThat(vacancyFilter.getId()).isNotNull();
    }

    @Test
    public void findById() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        session.flush();
        session.clear();

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyFilter);
    }

    @Test
    public void update() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        session.flush();

        vacancyFilter.setName("newName");
        vacancyFilterRepository.update(vacancyFilter);
        session.flush();
        session.clear();

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyFilter);
    }

    @Test
    public void delete() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        session.flush();

        vacancyFilterRepository.delete(vacancyFilter);
        session.flush();
        session.clear();

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static VacancyFilter getVacancyFilter() {
        VacancyFilter vacancyFilter =
                VacancyFilter.builder()
                        .code("default")
                        .name("Фильтр по умолчанию")
                        .createdAt(Instant.now())
                        .modifiedAt(Instant.now())
                        .build();

        vacancyFilter.addParam(
                VacancyFilterParams.builder().paramName("city").paramValue("1").build());

        vacancyFilter.addParam(
                VacancyFilterParams.builder().paramName("text").paramValue("java").build());

        return vacancyFilter;
    }
}
