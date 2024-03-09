package ru.mindils.jb.sync.service;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.util.HibernateUtil;

public class SyncVacancyAIService {

    private static final SyncVacancyAIService INSTANCE = new SyncVacancyAIService();
    private final VacancyClientService vacancyApiClientService = VacancyClientService.getInstance();

    private SyncVacancyAIService() {}

    public static SyncVacancyAIService getInstance() {
        return INSTANCE;
    }

    /**
     * TODO: переписать, тут надо чтобы мы получали вакансии где нет рейтинга и обновляли его в
     * данном случае может быть не создана запись в vacancy_info и мы не обновим рейтинг
     */
    public void syncVacancyAiRatingsAll() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
                Session session = sessionFactory.openSession()) {

            while (true) {
                List<VacancyInfo> vacancies =
                        session.createQuery(
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
    }

    public void syncVacancyAiRating(VacancyInfo vacancyInfo) {
        String sb =
                vacancyInfo.getVacancy().getName() + " " + vacancyInfo.getVacancy().getKeySkills();
        String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

        SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            vacancyInfo.setAiApproved(new BigDecimal(ratingAi));

            session.merge(vacancyInfo);

            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
            sessionFactory.close();
        }
    }
}
