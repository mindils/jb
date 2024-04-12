package ru.mindils.jb.service.service.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.SystemVacancyFilterDto;
import ru.mindils.jb.service.entity.Vacancy;

@UtilityClass
public class VacancyCriteriaApiFilterBuilder {

  public static Predicate[] build(
      AppVacancyFilterDto filter, CriteriaBuilder cb, Root<Vacancy> root) {
    List<Predicate> predicates = new ArrayList<>();
    SystemVacancyFilterDto systemFilter =
        SystemVacancyFilterDto.builder().aiApproved(BigDecimal.valueOf(0.7)).build();
    if (systemFilter.getAiApproved() != null) {
      predicates.add(
          cb.greaterThan(root.get("vacancyInfo").get("aiApproved"), systemFilter.getAiApproved()));
    }

    if (filter.getSalaryFrom() != null) {
      predicates.add(cb.or(
          cb.isNull(root.get("salary").get("from")),
          cb.greaterThan(root.get("salary").get("from"), filter.getSalaryFrom())));
    }

    if (filter.getSalaryTo() != null) {
      predicates.add(cb.or(
          cb.isNull(root.get("salary").get("to")),
          cb.lessThan(root.get("salary").get("to"), filter.getSalaryTo())));
    }

    if (filter.getStatus() != null) {
      predicates.add(cb.equal(root.get("vacancyInfo").get("status"), filter.getStatus()));
    }

    return predicates.toArray(new Predicate[0]);
  }
}
