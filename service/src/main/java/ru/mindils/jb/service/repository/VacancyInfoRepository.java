package ru.mindils.jb.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.VacancyInfo;

@Repository
public interface VacancyInfoRepository extends JpaRepository<VacancyInfo, Long> {
}
