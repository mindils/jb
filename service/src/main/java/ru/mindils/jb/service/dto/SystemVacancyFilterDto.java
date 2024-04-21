package ru.mindils.jb.service.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

@Value
@Builder
public class SystemVacancyFilterDto {

  BigDecimal aiApproved;
  VacancyStatusEnum status;
  boolean archived;
}
