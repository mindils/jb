package ru.mindils.jb.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mindils.jb.sync.entity.SyncLog;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
}
