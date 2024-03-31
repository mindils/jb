package ru.mindils.jb.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;

@Service
@RequiredArgsConstructor
public class VacancyService {

  private final VacancyRepository vacancyRepository;

  public Page<Vacancy> findAll(AppVacancyFilterDto filter, Pageable pageable) {
    return vacancyRepository.findAll(VacancyQueryDslFilterBuilder.build(filter), pageable);
  }

  public Vacancy findById(String id) {
    return vacancyRepository.findById(id).orElseThrow();
  }

  public void updateStatus(String id, VacancyStatusEnum status) {
    vacancyRepository.findById(id).ifPresent(vacancy -> {
      vacancy.getVacancyInfo().setStatus(status);
      vacancyRepository.save(vacancy);
    });
  }
}
