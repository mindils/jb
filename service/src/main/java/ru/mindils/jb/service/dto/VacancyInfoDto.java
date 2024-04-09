package ru.mindils.jb.service.dto;

import java.math.BigDecimal;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

public record VacancyInfoDto(
    Long id, BigDecimal aiApproved, Boolean approved, VacancyStatusEnum status) {
}
