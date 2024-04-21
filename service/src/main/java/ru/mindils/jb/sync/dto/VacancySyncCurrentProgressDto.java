package ru.mindils.jb.sync.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancySyncCurrentProgressDto {

  private boolean finished;
  private long current;
  private long total;
}
