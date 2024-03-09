package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import ru.mindils.jb.service.entity.Employer;

public class EmployerRepository extends RepositoryBase<String, Employer> {

    public EmployerRepository(EntityManager entityManager) {
        super(Employer.class, entityManager);
    }
}
