package ru.mindils.jb.service.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.VacancyInfo;

@Repository
public interface VacancyInfoRepository extends JpaRepository<VacancyInfo, Long> {

  @Modifying(clearAutomatically = true)
  @Query(
      value =
          "UPDATE jb_vacancy_info SET ai_approved = null WHERE jb_vacancy_info.vacancy_id IN (SELECT id FROM jb_vacancy WHERE internal_modified_at < :threshold)",
      nativeQuery = true)
  int updateAiApprovedToNullByThreshold(@Param("threshold") LocalDateTime threshold);
}
