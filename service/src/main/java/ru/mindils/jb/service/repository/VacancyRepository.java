package ru.mindils.jb.service.repository;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.Vacancy;

@Repository
public interface VacancyRepository
        extends JpaRepository<Vacancy, String>, QuerydslPredicateExecutor<Vacancy> {

    Slice<Vacancy> findAllByDetailed(boolean detailed, PageRequest pageable);

    @Query("select e from Vacancy e where e.id in :ids")
    List<Vacancy> findByIdIn(List<String> ids);

    @Query("select e from Vacancy e left join e.vacancyInfo i where i.aiApproved is null")
    Slice<Vacancy> findVacanciesWithoutAiApproved(PageRequest pageable);
}
