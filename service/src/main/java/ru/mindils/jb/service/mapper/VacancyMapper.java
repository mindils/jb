package ru.mindils.jb.service.mapper;

import org.mapstruct.Mapper;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.entity.Vacancy;

@Mapper(componentModel = "spring", implementationName = "ServiceVacancyMapper")
public interface VacancyMapper {

  UpdateStatusVacancyDto map(Vacancy entity);
}
