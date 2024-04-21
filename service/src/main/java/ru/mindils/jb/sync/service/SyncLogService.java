package ru.mindils.jb.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.sync.entity.SyncLog;
import ru.mindils.jb.sync.entity.SyncLogStatus;
import ru.mindils.jb.sync.entity.SyncLogType;
import ru.mindils.jb.sync.repository.SyncLogRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncLogService {

  private final SyncLogRepository syncLogRepository;

  @SneakyThrows
  public void saveLog(
      String id, String objects, SyncLogType syncLogType, SyncLogStatus syncLogStatus) {

    syncLogRepository.saveAndFlush(SyncLog.builder()
        .entityId(id)
        .dataType(syncLogType)
        .data(objects)
        .status(syncLogStatus)
        .build());
  }
}
