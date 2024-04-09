package ru.mindils.jb.service.service.util;

import com.querydsl.core.types.Predicate;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.SystemVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

@UtilityClass
public class VacancyQueryDslFilterBuilder {

  public static Predicate build(AppVacancyFilterDto filter) {
    SystemVacancyFilterDto systemFilter = SystemVacancyFilterDto.builder()
        .aiApproved(BigDecimal.valueOf(0.7))
//        .status(VacancyStatusEnum.ARCHIVED)
        .build();
    return QPredicate.builder()
        .add(systemFilter.getAiApproved(), QVacancy.vacancy.vacancyInfo.aiApproved::gt)
//        .add(systemFilter.getStatus(), QVacancy.vacancy.vacancyInfo.status::ne)
        .add(filter.getSalaryFrom(), salaryFrom -> QVacancy.vacancy
            .salary
            .from
            .isNull()
            .or(QVacancy.vacancy.salary.from.gt(filter.getSalaryFrom())))
        .add(
            filter.getSalaryTo(),
            salaryTo ->
                QVacancy.vacancy.salary.to.isNull().or(QVacancy.vacancy.salary.to.lt(salaryTo)))
        .add(filter.getStatus(), QVacancy.vacancy.vacancyInfo.status::eq)
        .buildAnd();
  }
}
