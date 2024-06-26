package ru.mindils.jb.service.repository;

import com.querydsl.core.types.Predicate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mindils.jb.service.entity.Vacancy;

public interface FilterVacancyRepository {

  Page<Vacancy> findAllByFilter(Predicate predicate, Pageable pageable);

  List<Vacancy> findAllByFilter(Predicate predicate);
}
