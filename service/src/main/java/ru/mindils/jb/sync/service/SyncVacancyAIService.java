package ru.mindils.jb.sync.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.repository.VacancyRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncVacancyAIService {

    private final VacancyClientService vacancyApiClientService;
    private final VacancyRepository vacancyRepository;

    public boolean syncVacancyAiRatingsBatch() {
        Slice<Vacancy> vacancies = vacancyRepository.findVacanciesWithoutAiApproved(PageRequest.of(0, 100));

        vacancies.forEach(this::syncVacancyAiRating);

        return !vacancies.isEmpty();
    }

    public void syncVacancyAiRating(Vacancy vacancy) {
        String sb = vacancy.getName() + " " + vacancy.getKeySkills();
        String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

        VacancyInfo vacancyInfo = vacancy.getVacancyInfo();

        if (vacancyInfo == null) {
            vacancyInfo = VacancyInfo.builder()
                    .vacancy(vacancy)
                    .status(VacancyStatusEnum.NEW)
                    .build();

            vacancy.setVacancyInfo(vacancyInfo);
        }

        vacancyInfo.setAiApproved(new BigDecimal(ratingAi));
        vacancyRepository.save(vacancy);
    }
}
