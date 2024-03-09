package ru.mindils.jb.service.repository;

import jakarta.persistence.EntityManager;
import ru.mindils.jb.service.entity.User;

public class UserRepository extends RepositoryBase<Long, User> {

    public UserRepository(EntityManager entityManager) {
        super(User.class, entityManager);
    }
}
