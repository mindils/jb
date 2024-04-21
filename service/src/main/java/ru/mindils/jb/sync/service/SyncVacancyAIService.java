package ru.mindils.jb.sync.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.repository.VacancyRepository;
import ru.mindils.jb.sync.dto.VacancySyncCurrentProgressDto;
import ru.mindils.jb.sync.entity.SyncLogStatus;
import ru.mindils.jb.sync.entity.SyncLogType;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncVacancyAIService {

  private final VacancyClientService vacancyApiClientService;
  private final VacancyRepository vacancyRepository;
  private final SyncLogService syncLogService;

  public VacancySyncCurrentProgressDto syncVacancyAiRatingsBatch() {
    Page<Vacancy> vacancies =
        vacancyRepository.findVacanciesWithoutAiApproved(PageRequest.of(0, 100));

    vacancies.forEach(this::syncVacancyAiRating);

    return VacancySyncCurrentProgressDto.builder()
        .total(vacancies.getTotalElements())
        .current(vacancies.getNumberOfElements())
        .finished(vacancies.isEmpty())
        .build();
  }

  public void syncVacancyAiRating(Vacancy vacancy) {
    String sb = vacancy.getName() + " " + vacancy.getKeySkills();
    var responseWrapperSync = vacancyApiClientService.loadAIRatingByText(sb);
    String ratingAi = responseWrapperSync.getData();

    VacancyInfo vacancyInfo = vacancy.getVacancyInfo();

    if (vacancyInfo == null) {
      vacancyInfo =
          VacancyInfo.builder().vacancy(vacancy).status(VacancyStatusEnum.NEW).build();

      vacancy.setVacancyInfo(vacancyInfo);
    }

    vacancyInfo.setAiApproved(new BigDecimal(ratingAi));
    vacancyRepository.saveAndFlush(vacancy);

    syncLogService.saveLog(
        vacancy.getId(),
        responseWrapperSync.getResponse().body(),
        SyncLogType.VACANCY_AI,
        SyncLogStatus.SUCCESS);
  }
}
