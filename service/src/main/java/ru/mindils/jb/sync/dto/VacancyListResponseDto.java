package ru.mindils.jb.sync.dto;

import java.util.List;
import lombok.Data;

@Data
public class VacancyListResponseDto {

  private List<BriefVacancyDto> items;
  private Long found;
  private Long pages;
  private Long perPage;
  private Integer page;

}
