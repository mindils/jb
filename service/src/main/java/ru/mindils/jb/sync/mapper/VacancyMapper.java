package ru.mindils.jb.sync.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.KeySkillDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.service.entity.Vacancy;

@Mapper(uses = {EmployerMapper.class})
public interface VacancyMapper {

  VacancyMapper INSTANCE = Mappers.getMapper(VacancyMapper.class);

  @Mapping(source = "area.name", target = "city")
  @Mapping(source = "type.id", target = "type")
  @Mapping(source = "schedule.id", target = "schedule")
  @Mapping(source = "employment.id", target = "employment")
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "keySkills", ignore = true)
  @Mapping(target = "vacancyInfo", ignore = true)
  @Mapping(target = "internalCreatedAt", ignore = true)
  @Mapping(target = "internalModifiedAt", ignore = true)
  Vacancy map(BriefVacancyDto entity);

  @Mapping(source = "area.name", target = "city")
  @Mapping(source = "type.id", target = "type")
  @Mapping(source = "schedule.id", target = "schedule")
  @Mapping(source = "employment.id", target = "employment")
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "keySkills", ignore = true)
  @Mapping(target = "vacancyInfo", ignore = true)
  @Mapping(target = "internalCreatedAt", ignore = true)
  @Mapping(target = "internalModifiedAt", ignore = true)
  Vacancy map(BriefVacancyDto entity, @MappingTarget Vacancy vacancy);

  @Mapping(source = "area.name", target = "city")
  @Mapping(source = "type.id", target = "type")
  @Mapping(source = "schedule.id", target = "schedule")
  @Mapping(source = "employment.id", target = "employment")
  @Mapping(source = "keySkills", target = "keySkills", qualifiedByName = "mapKeySkillsToString")
  @Mapping(target = "vacancyInfo", ignore = true)
  @Mapping(target = "internalCreatedAt", ignore = true)
  @Mapping(target = "internalModifiedAt", ignore = true)
  Vacancy map(DetailedVacancyDto entity, @MappingTarget Vacancy vacancy);

  @Named("mapKeySkillsToString")
  default String mapKeySkillsToString(List<KeySkillDto> keySkills) {
    if (keySkills == null) {
      return null;
    }
    return keySkills.stream().map(KeySkillDto::getName).filter(Objects::nonNull)
        .collect(Collectors.joining(", "));
  }
}
