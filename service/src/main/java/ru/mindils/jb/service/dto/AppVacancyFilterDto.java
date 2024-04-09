package ru.mindils.jb.service.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

@Value
@Builder
public class AppVacancyFilterDto {

  Integer salaryFrom;
  Integer salaryTo;
  VacancyStatusEnum status;
}
