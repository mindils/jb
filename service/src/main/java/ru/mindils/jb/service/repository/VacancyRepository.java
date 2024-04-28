package ru.mindils.jb.service.repository;

import com.querydsl.core.types.Predicate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.Vacancy;

@Repository
public interface VacancyRepository
    extends JpaRepository<Vacancy, String>,
        QuerydslPredicateExecutor<Vacancy>,
        FilterVacancyRepository {

  @EntityGraph(attributePaths = {"employer", "employer.employerInfo", "vacancyInfo"})
  Page<Vacancy> findAll(Predicate predicate, Pageable pageable);

  Page<Vacancy> findAllByDetailed(boolean detailed, PageRequest pageable);

  @Query("select e from Vacancy e where e.id in :ids")
  List<Vacancy> findByIdIn(List<String> ids);

  @Query("select e from Vacancy e left join e.vacancyInfo i where i.aiApproved is null")
  Page<Vacancy> findVacanciesWithoutAiApproved(PageRequest pageable);

  @Query("select count(e) from Vacancy e left join e.vacancyInfo i where i.aiApproved is null")
  long countVacanciesWithoutAiApproved();

  @Query("select count(e) from Vacancy e where e.detailed = false or e.detailed is null")
  long countVacanciesDetailedFalse();

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          "UPDATE jb_vacancy SET detailed = false WHERE jb_vacancy.internal_modified_at < :threshold",
      nativeQuery = true)
  int updateDetailedBy(@Param("threshold") LocalDate threshold);
}
