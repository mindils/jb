package ru.mindils.jb.service.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.mindils.jb.service.entity.QEmployer;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;

@RequiredArgsConstructor
public class FilterVacancyRepositoryImpl implements FilterVacancyRepository {

  private final EntityManager entityManager;

  @Override
  public Page<Vacancy> findAllByFilter(Predicate predicate, Pageable pageable) {
    JPAQuery<Vacancy> query = createBaseQuery(predicate);
    addSortingToQuery(query, pageable);
    List<Vacancy> vacancies = fetchVacancies(query, pageable);
    long totalCount = fetchTotalCount(predicate);
    return new PageImpl<>(vacancies, pageable, totalCount);
  }

  @Override
  public List<Vacancy> findAllByFilter(Predicate predicate) {
    JPAQuery<Vacancy> query = createBaseQuery(predicate);
    return query.fetch();
  }

  private JPAQuery<Vacancy> createBaseQuery(Predicate predicate) {
    return new JPAQuery<>(entityManager)
        .select(QVacancy.vacancy)
        .from(QVacancy.vacancy)
        .leftJoin(QVacancy.vacancy.vacancyInfo)
        .fetchJoin()
        .leftJoin(QVacancy.vacancy.employer)
        .fetchJoin()
        .leftJoin(QEmployer.employer.employerInfo)
        .fetchJoin()
        .where(predicate);
  }

  private void addSortingToQuery(JPAQuery<Vacancy> query, Pageable pageable) {
    if (pageable.getSort().isSorted()) {
      pageable.getSort().forEach(order -> {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;
        PathBuilder<Vacancy> pathBuilder = new PathBuilder<>(Vacancy.class, "vacancy");
        query.orderBy(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
      });
    }
  }

  private List<Vacancy> fetchVacancies(JPAQuery<Vacancy> query, Pageable pageable) {
    return query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
  }

  private long fetchTotalCount(Predicate predicate) {
    return new JPAQuery<>(entityManager)
        .select(QVacancy.vacancy)
        .from(QVacancy.vacancy)
        .leftJoin(QVacancy.vacancy.vacancyInfo)
        .leftJoin(QVacancy.vacancy.employer)
        .leftJoin(QEmployer.employer.employerInfo)
        .where(predicate)
        .fetchCount();
  }
}
