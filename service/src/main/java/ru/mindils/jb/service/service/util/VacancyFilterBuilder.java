package ru.mindils.jb.service.service.util;

import com.querydsl.core.types.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;

@UtilityClass
public class VacancyFilterBuilder {

  public static jakarta.persistence.criteria.Predicate[] buildCriteriaApiFilter(
      AppVacancyFilterDto filter,
      CriteriaBuilder cb, Root<Vacancy> root) {
    List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

    if (filter.getAiApproved() != null) {
      predicates.add(
          cb.greaterThan(root.get("vacancyInfo").get("aiApproved"), filter.getAiApproved()));
    }

    if (filter.getSalaryFrom() != null) {
      predicates.add(cb.or(
          cb.isNull(root.get("salary").get("from")),
          cb.greaterThan(root.get("salary").get("from"), filter.getSalaryFrom())
      ));
    }

    if (filter.getSalaryTo() != null) {
      predicates.add(cb.or(
          cb.isNull(root.get("salary").get("to")),
          cb.lessThan(root.get("salary").get("to"), filter.getSalaryTo())
      ));
    }

    if (filter.getStatus() != null) {
      predicates.add(cb.equal(root.get("vacancyInfo").get("status"), filter.getStatus()));
    }

    return predicates.toArray(new jakarta.persistence.criteria.Predicate[0]);
  }

  /**
   * <p>
   * Пока не придумал, как сделать этот метод более читаемым
   * <br>т.к. условия разные для разных полей
   */
  public static Predicate[] buildQueryDslFilter(AppVacancyFilterDto filter) {
    List<Predicate> predicates = new ArrayList<>();

    if (filter.getAiApproved() != null) {
      predicates.add(QVacancy.vacancy.vacancyInfo.aiApproved.gt(filter.getAiApproved()));
    }

    if (filter.getSalaryFrom() != null) {
      predicates.add(QVacancy.vacancy.salary.from.isNull()
          .or(QVacancy.vacancy.salary.from.gt(filter.getSalaryFrom())));
    }

    if (filter.getSalaryTo() != null) {
      predicates.add(QVacancy.vacancy.salary.to.isNull()
          .or(QVacancy.vacancy.salary.to.lt(filter.getSalaryTo())));
    }

    if (filter.getStatus() != null) {
      predicates.add(QVacancy.vacancy.vacancyInfo.status.eq(filter.getStatus()));
    }
    return predicates.toArray(Predicate[]::new);
  }
}
