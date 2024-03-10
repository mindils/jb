package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.VacancyFilter;

@Repository
public class VacancyFilterRepository extends RepositoryBase<Long, VacancyFilter> {

    public VacancyFilterRepository(EntityManager entityManager) {
        super(VacancyFilter.class, entityManager);
    }
}
