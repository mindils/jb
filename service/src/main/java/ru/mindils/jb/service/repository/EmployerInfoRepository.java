package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.EmployerInfo;

@Repository
public class EmployerInfoRepository extends RepositoryBase<Long, EmployerInfo> {

    public EmployerInfoRepository(EntityManager entityManager) {
        super(EmployerInfo.class, entityManager);
    }
}
