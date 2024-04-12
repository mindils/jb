package ru.mindils.jb.service.mapper;

import org.mapstruct.Mapper;
import ru.mindils.jb.service.dto.EmployerReadDto;
import ru.mindils.jb.service.entity.Employer;

@Mapper(componentModel = "spring")
public interface EmployerUpdateStatusMapper {

  EmployerReadDto map(Employer entity);
}
