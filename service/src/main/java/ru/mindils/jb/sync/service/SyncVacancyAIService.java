package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.entity.VacancyInfo;

@Service
@RequiredArgsConstructor
public class SyncVacancyAIService {

    private final VacancyClientService vacancyApiClientService;
    private final EntityManager entityManager;

    /**
     * TODO: переписать, тут надо чтобы мы получали вакансии где нет рейтинга и обновляли его в
     * данном случае может быть не создана запись в vacancy_info и мы не обновим рейтинг
     */
    public void syncVacancyAiRatingsAll() {

        while (true) {
            List<VacancyInfo> vacancies =
                    entityManager
                            .createQuery(
                                    "select e from VacancyInfo e join e.vacancy where"
                                            + " e.aiApproved is null",
                                    VacancyInfo.class)
                            .setFirstResult(0)
                            .setMaxResults(100)
                            .getResultList();

            if (vacancies.isEmpty()) {
                break;
            }

            // тут можно без сна, т.к. наш сервер )
            vacancies.forEach(this::syncVacancyAiRating);
        }
    }

    public void syncVacancyAiRating(VacancyInfo vacancyInfo) {
        String sb =
                vacancyInfo.getVacancy().getName() + " " + vacancyInfo.getVacancy().getKeySkills();
        String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

        entityManager.getTransaction().begin();
        vacancyInfo.setAiApproved(new BigDecimal(ratingAi));
        entityManager.merge(vacancyInfo);
        entityManager.getTransaction().commit();
    }
}
