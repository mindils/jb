package ru.mindils.jb.service.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import java.util.List;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;

public class VacancyRepository extends RepositoryBase<String, Vacancy> {

  public VacancyRepository(EntityManager entityManager) {
    super(Vacancy.class, entityManager);
  }

  public List<Vacancy> findByFilter(AppVacancyFilterDto filter) {
    JPAQuery<Vacancy> vacancyJPAQuery = new JPAQuery<>(getEntityManager());

    return vacancyJPAQuery
        .select(QVacancy.vacancy)
        .from(QVacancy.vacancy)
        .leftJoin(QVacancy.vacancy.vacancyInfo).fetchJoin()
        .where(VacancyQueryDslFilterBuilder.build(filter))
        .fetch();
  }


}
