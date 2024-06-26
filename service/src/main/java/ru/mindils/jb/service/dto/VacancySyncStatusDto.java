package ru.mindils.jb.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancySyncStatusDto {

  private Boolean syncRunning;
  private String statusText;
  private Integer progress;
  private Integer step;
  private long totalElements;
  private long currentElement;
}
