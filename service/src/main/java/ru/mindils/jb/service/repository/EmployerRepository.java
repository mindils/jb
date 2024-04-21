package ru.mindils.jb.service.repository;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.Employer;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {

  Page<Employer> findAllByDetailed(boolean detailed, Pageable pageable);

  @Query("select count(e) from Employer e where e.detailed = false or e.detailed is null")
  long countEmployerWithoutDetailed();

  @Modifying(clearAutomatically = true)
  @Query(
      value = "UPDATE jb_employer SET detailed = false WHERE modified_at < :threshold",
      nativeQuery = true)
  int updateDetailedBy(@Param("threshold") LocalDate threshold);
}
