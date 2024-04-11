package ru.mindils.jb.service.dto;

import java.util.List;
import java.util.Map;

public record EmployerReadDto(
    String id,
    String name,
    Boolean trusted,
    String description,
    Boolean detailed,
    Boolean accreditedItEmployer,
    String type,
    String siteUrl,
    String alternateUrl,
    String logoUrlsOriginal,
    String areaName,
    List<Map<String, ?>> industries) {
}
