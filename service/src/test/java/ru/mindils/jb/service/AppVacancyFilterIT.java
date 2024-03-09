package ru.mindils.jb.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.service.util.VacancyCriteriaApiFilterBuilder;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;
import ru.mindils.jb.service.util.HibernateTestUtil;
import ru.mindils.jb.service.util.TestDataImporter;

public class AppVacancyFilterIT {

    private static SessionFactory sessionFactory;
    private Session session;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    static void tearDownAll() {
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
    void queryCriteriaApiApproved() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .aiApproved(BigDecimal.valueOf(0.7))
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(100000)
                        .salaryTo(300000)
                        .build();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Vacancy> criteria = cb.createQuery(Vacancy.class);
        Root<Vacancy> vacancy = criteria.from(Vacancy.class);
        vacancy.fetch("vacancyInfo", JoinType.LEFT);

        criteria.select(vacancy).where(VacancyCriteriaApiFilterBuilder.build(filter, cb, vacancy));

        List<Vacancy> actualResult = session.createQuery(criteria).getResultList();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void queryCriteriaApiNoMatch() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(500000)
                        .salaryTo(1000000)
                        .build();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Vacancy> criteria = cb.createQuery(Vacancy.class);
        Root<Vacancy> vacancy = criteria.from(Vacancy.class);

        vacancy.fetch("vacancyInfo", JoinType.LEFT);
        criteria.select(vacancy).where(VacancyCriteriaApiFilterBuilder.build(filter, cb, vacancy));

        List<Vacancy> actualResult = session.createQuery(criteria).getResultList();

        assertThat(actualResult).isEmpty();
    }

    @Test
    void queryDsqApproved() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .aiApproved(BigDecimal.valueOf(0.7))
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(100000)
                        .salaryTo(300000)
                        .build();

        JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(session);

        List<Vacancy> actualResult =
                vacancyJPAQuery
                        .select(QVacancy.vacancy)
                        .from(QVacancy.vacancy)
                        .leftJoin(QVacancy.vacancy.vacancyInfo)
                        .fetchJoin()
                        .where(VacancyQueryDslFilterBuilder.build(filter))
                        .fetch();

        assertThat(actualResult).hasSize(1);
    }

    @Test
    void queryDsqNoMatch() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(500000)
                        .salaryTo(1000000)
                        .build();

        JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(session);

        List<Vacancy> actualResult =
                vacancyJPAQuery
                        .select(QVacancy.vacancy)
                        .from(QVacancy.vacancy)
                        .leftJoin(QVacancy.vacancy.vacancyInfo)
                        .fetchJoin()
                        .where(VacancyQueryDslFilterBuilder.build(filter))
                        .fetch();

        assertThat(actualResult).isEmpty();
    }
}
