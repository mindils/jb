package ru.mindils.jb.service.dto;


public record EmployerDto(
    String id, String name, Boolean trusted, String description, Boolean detailed) {
}
