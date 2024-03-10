package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.Employer;

@Repository
public class EmployerRepository extends RepositoryBase<String, Employer> {

    public EmployerRepository(EntityManager entityManager) {
        super(Employer.class, entityManager);
    }
}
