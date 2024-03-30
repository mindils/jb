package ru.mindils.jb.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.VacancyFilter;

@Repository
public interface VacancyFilterRepository extends JpaRepository<VacancyFilter, Long> {
}
