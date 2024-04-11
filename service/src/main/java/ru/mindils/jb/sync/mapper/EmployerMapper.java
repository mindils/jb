package ru.mindils.jb.sync.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.sync.dto.BriefEmployerDto;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;

@Mapper(componentModel = "spring")
public interface EmployerMapper {

  @Mapping(target = "id", source = "id", defaultValue = "0")
  @Mapping(target = "trusted", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "vacancy", ignore = true)
  @Mapping(target = "employerInfo", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(
      target = "detailed",
      expression = "java(dto.getId() == null || dto.getId().equals(\"0\"))")
  Employer map(BriefEmployerDto dto);

  @Mapping(target = "trusted", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "vacancy", ignore = true)
  @Mapping(target = "employerInfo", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(target = "id", source = "id", defaultValue = "0")
  @Mapping(
      target = "detailed",
      expression = "java(entity.getId() == null || entity.getId().equals(\"0\"))")
  Employer map(BriefEmployerDto entity, @MappingTarget Employer employer);

  @Mapping(target = "trusted", ignore = true)
  @Mapping(target = "vacancy", ignore = true)
  @Mapping(target = "employerInfo", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(target = "id", source = "id", defaultValue = "0")
  @Mapping(target = "detailed", defaultValue = "true")
  @Mapping(source = "logoUrls.original", target = "logoUrlsOriginal")
  @Mapping(source = "area.name", target = "areaName")
  Employer map(DetailedEmployerDto entity, @MappingTarget Employer employer);
}
