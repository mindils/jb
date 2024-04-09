package ru.mindils.jb.service.mapper;

import org.mapstruct.Mapper;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.entity.Vacancy;

@Mapper(componentModel = "spring")
public interface VacancyReadMapper {

  VacancyReadDto map(Vacancy entity);
}
