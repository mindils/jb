package ru.mindils.jb.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mindils.jb.service.config.ApplicationConfiguration;
import ru.mindils.jb.service.config.TestApplicationConfiguration;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.service.util.VacancyCriteriaApiFilterBuilder;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;
import ru.mindils.jb.service.util.TestDataImporter;

public class AppVacancyFilterIT {

    private static EntityManager entityManager;

    @BeforeAll
    static void setUpAll() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("test");
        context.register(TestApplicationConfiguration.class, ApplicationConfiguration.class);
        context.refresh();
        entityManager = context.getBean(EntityManager.class);
        TestDataImporter.importData(entityManager);
    }

    @BeforeEach
    void setUp() {
        entityManager.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        entityManager.getTransaction().rollback();
        entityManager.close();
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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> criteria = cb.createQuery(Vacancy.class);
        Root<Vacancy> vacancy = criteria.from(Vacancy.class);
        vacancy.fetch("vacancyInfo", JoinType.LEFT);

        criteria.select(vacancy).where(VacancyCriteriaApiFilterBuilder.build(filter, cb, vacancy));

        List<Vacancy> actualResult = entityManager.createQuery(criteria).getResultList();

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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> criteria = cb.createQuery(Vacancy.class);
        Root<Vacancy> vacancy = criteria.from(Vacancy.class);

        vacancy.fetch("vacancyInfo", JoinType.LEFT);
        criteria.select(vacancy).where(VacancyCriteriaApiFilterBuilder.build(filter, cb, vacancy));

        List<Vacancy> actualResult = entityManager.createQuery(criteria).getResultList();

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

        JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(entityManager);

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

        JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(entityManager);

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
