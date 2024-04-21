package ru.mindils.jb.sync.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mindils.jb.sync.entity.VacancySyncExecution;

public interface VacancySyncExecutionRepository extends JpaRepository<VacancySyncExecution, Long> {

  @Query(
      "select e from VacancySyncExecution e where e.status = ru.mindils.jb.sync.entity.VacancySyncStatus.NEW order by e.priority,"
          + " e.step limit 1")
  Optional<VacancySyncExecution> findRunningJob();
}
