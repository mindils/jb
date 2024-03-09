package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import ru.mindils.jb.service.entity.EmployerInfo;

public class EmployerInfoRepository extends RepositoryBase<Long, EmployerInfo> {

    public EmployerInfoRepository(EntityManager entityManager) {
        super(EmployerInfo.class, entityManager);
    }
}
