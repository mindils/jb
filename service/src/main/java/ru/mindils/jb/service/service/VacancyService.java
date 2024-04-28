package ru.mindils.jb.service.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.dto.UpdateStatusVacancyDto;
import ru.mindils.jb.service.dto.VacancyReadDto;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.mapper.VacancyMapper;
import ru.mindils.jb.service.mapper.VacancyReadMapper;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacancyService {

  private final VacancyRepository vacancyRepository;
  private final VacancyMapper vacancyMapper;
  private final VacancyReadMapper vacancyReadMapper;

  public Page<VacancyReadDto> findAll(AppVacancyFilterDto filter, Pageable pageable) {
    return vacancyRepository
        .findAllByFilter(VacancyQueryDslFilterBuilder.build(filter), pageable)
        .map(vacancyReadMapper::map);
  }

  @Transactional
  public void setDetailedFalse(AppVacancyFilterDto filter) {
    var allByFilter = vacancyRepository.findAllByFilter(VacancyQueryDslFilterBuilder.build(filter));
    allByFilter.stream()
        .peek(vacancy -> vacancy.setDetailed(false))
        .forEach(vacancyRepository::saveAndFlush);
  }

  public VacancyReadDto findById(String id) {
    return vacancyRepository.findById(id).map(vacancyReadMapper::map).orElseThrow();
  }

  @Transactional
  public Optional<UpdateStatusVacancyDto> updateStatus(String id, UpdateStatusVacancyDto dto) {
    return vacancyRepository
        .findById(id)
        .map(vacancy -> {
          VacancyInfo vacancyInfo = vacancy.getVacancyInfo();
          if (vacancyInfo == null) {
            vacancyInfo =
                VacancyInfo.builder().vacancy(vacancy).status(dto.status()).build();
            vacancy.setVacancyInfo(vacancyInfo);
          } else {
            vacancyInfo.setStatus(dto.status());
          }
          return vacancy;
        })
        .map(vacancy -> {
          try {
            return vacancyRepository.saveAndFlush(vacancy);
          } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockingFailureException(
                "Vacancy status has been updated by another user");
          }
        })
        .map(vacancyMapper::map);
  }
}
