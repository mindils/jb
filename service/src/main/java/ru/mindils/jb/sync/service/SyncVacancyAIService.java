package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncVacancyAIService {

    private final VacancyClientService vacancyApiClientService;
    private final EntityManager entityManager;

    public boolean syncVacancyAiRatingsBatch() {
        List<Vacancy> vacancies =
                entityManager
                        .createQuery(
                                "select e from Vacancy e left join e.vacancyInfo i where"
                                        + " i.aiApproved is null",
                                Vacancy.class)
                        .setMaxResults(100)
                        .getResultList();

        vacancies.forEach(this::syncVacancyAiRating);

        return !vacancies.isEmpty();
    }

    public void syncVacancyAiRating(Vacancy vacancy) {
        String sb = vacancy.getName() + " " + vacancy.getKeySkills();
        String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

        VacancyInfo vacancyInfo = vacancy.getVacancyInfo();

        if (vacancyInfo == null) {
            vacancyInfo =
                    VacancyInfo.builder().vacancy(vacancy).status(VacancyStatusEnum.NEW).build();

            vacancy.setVacancyInfo(vacancyInfo);
        }

        vacancyInfo.setAiApproved(new BigDecimal(ratingAi));
        entityManager.merge(vacancy);
    }
}
