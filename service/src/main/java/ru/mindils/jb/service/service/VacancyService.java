package ru.mindils.jb.service.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.mapper.VacancyMapper;
import ru.mindils.jb.service.mapper.VacancyReadMapper;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;

@Service
@RequiredArgsConstructor
public class VacancyService {

  private final VacancyRepository vacancyRepository;
  private final VacancyMapper vacancyMapper;
  private final VacancyReadMapper vacancyReadMapper;


  public Page<VacancyReadDto> findAll(AppVacancyFilterDto filter, Pageable pageable) {
    return vacancyRepository.findAll(VacancyQueryDslFilterBuilder.build(filter), pageable)
        .map(vacancyReadMapper::map);
  }

  public Vacancy findById(String id) {
    return vacancyRepository.findById(id).orElseThrow();
  }

  public Optional<UpdateStatusVacancyDto> updateStatus(String id, UpdateStatusVacancyDto dto) {
    return vacancyRepository
        .findById(id)
        .map(entity -> {
          entity.getVacancyInfo().setStatus(dto.status());
          return entity;
        })
        .map(vacancyRepository::saveAndFlush)
        .map(vacancyMapper::map);
  }
}
