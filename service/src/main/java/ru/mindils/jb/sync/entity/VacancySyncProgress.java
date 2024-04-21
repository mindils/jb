package ru.mindils.jb.sync.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancySyncProgress {

  private long total;
  private long currentElement;
  private ProgressType type;
  private boolean filled;
  private boolean finished;
}
