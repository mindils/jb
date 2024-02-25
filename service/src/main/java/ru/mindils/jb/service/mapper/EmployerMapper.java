package ru.mindils.jb.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.mindils.jb.service.dto.BriefEmployerDto;
import ru.mindils.jb.service.dto.DetailedEmployerDto;
import ru.mindils.jb.service.entity.Employer;

@Mapper
public interface EmployerMapper {

  EmployerMapper INSTANCE = Mappers.getMapper(EmployerMapper.class);

  @Mapping(target = "id", source = "id", defaultValue = "0")
  Employer map(BriefEmployerDto dto);

  @Mapping(target = "id", source = "id", defaultValue = "0")
  Employer map(BriefEmployerDto entity, @MappingTarget Employer employer);

  @Mapping(target = "id", source = "id", defaultValue = "0")
  Employer map(DetailedEmployerDto entity, @MappingTarget Employer employer);
}
