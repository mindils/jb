package ru.mindils.jb.service.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.QVacancy;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.service.util.VacancyQueryDslFilterBuilder;

@Repository
public class VacancyRepository extends RepositoryBase<String, Vacancy> {

    public VacancyRepository(EntityManager entityManager) {
        super(Vacancy.class, entityManager);
    }

    public List<Vacancy> findByFilter(AppVacancyFilterDto filter) {
        return new JPAQuery<>(getEntityManager())
                .select(QVacancy.vacancy)
                .from(QVacancy.vacancy)
                .leftJoin(QVacancy.vacancy.vacancyInfo)
                .fetchJoin()
                .where(VacancyQueryDslFilterBuilder.build(filter))
                .fetch();
    }
}
