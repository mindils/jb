package ru.mindils.jb.service.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record VacancyReadDto(
    String id,
    String name,
    EmployerDto employer,
    Boolean premium,
    String city,
    SalaryDto salary,
    String type,
    Instant publishedAt,
    Instant createdAt,
    Boolean archived,
    String applyAlternateUrl,
    String url,
    String alternateUrl,
    String schedule,
    String responseUrl,
    List<Map<String, ?>> professionalRoles,
    String employment,
    String description,
    String keySkills,
    Boolean detailed,
    VacancyInfoDto vacancyInfo,
    Instant internalCreatedAt,
    Instant internalModifiedAt) {
}
