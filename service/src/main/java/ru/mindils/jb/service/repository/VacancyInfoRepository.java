package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import ru.mindils.jb.service.entity.VacancyInfo;

public class VacancyInfoRepository extends RepositoryBase<Long, VacancyInfo> {

    public VacancyInfoRepository(EntityManager entityManager) {
        super(VacancyInfo.class, entityManager);
    }
}
