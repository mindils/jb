package ru.mindils.jb.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.service.util.VacancyCriteriaApiFilterBuilder;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;

@RequiredArgsConstructor
public class AppVacancyFilterIT extends ITBase {

  private final EntityManager entityManager;

  @Test
  void queryCriteriaApiApproved() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder()
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
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder()
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
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder()
        .aiApproved(BigDecimal.valueOf(0.7))
        .status(VacancyStatusEnum.APPROVED)
        .salaryFrom(100000)
        .salaryTo(300000)
        .build();

    JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(entityManager);

    List<Vacancy> actualResult = vacancyJPAQuery
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
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder()
        .status(VacancyStatusEnum.APPROVED)
        .salaryFrom(500000)
        .salaryTo(1000000)
        .build();

    JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(entityManager);

    List<Vacancy> actualResult = vacancyJPAQuery
        .select(QVacancy.vacancy)
        .from(QVacancy.vacancy)
        .leftJoin(QVacancy.vacancy.vacancyInfo)
        .fetchJoin()
        .where(VacancyQueryDslFilterBuilder.build(filter))
        .fetch();

    assertThat(actualResult).isEmpty();
  }
}
