package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.VacancyInfo;

@Repository
public class VacancyInfoRepository extends RepositoryBase<Long, VacancyInfo> {

    public VacancyInfoRepository(EntityManager entityManager) {
        super(VacancyInfo.class, entityManager);
    }
}
