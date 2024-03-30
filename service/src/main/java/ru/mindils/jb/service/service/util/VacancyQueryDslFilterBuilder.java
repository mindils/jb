package ru.mindils.jb.service.service.util;

import com.querydsl.core.types.Predicate;
import lombok.experimental.UtilityClass;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;

@UtilityClass
public class VacancyQueryDslFilterBuilder {

    public static Predicate build(AppVacancyFilterDto filter) {
        return QPredicate.builder()
                .add(filter.getAiApproved(), QVacancy.vacancy.vacancyInfo.aiApproved::gt)
                .add(filter.getSalaryFrom(), salaryFrom -> QVacancy.vacancy
                        .salary
                        .from
                        .isNull()
                        .or(QVacancy.vacancy.salary.from.gt(filter.getSalaryFrom())))
                .add(
                        filter.getSalaryTo(),
                        salaryTo -> QVacancy.vacancy.salary.to.isNull().or(QVacancy.vacancy.salary.to.lt(salaryTo)))
                .add(filter.getStatus(), QVacancy.vacancy.vacancyInfo.status::eq)
                .buildAnd();
    }
}
