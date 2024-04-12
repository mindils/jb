package ru.mindils.jb.service.dto;

public record UserReadDto(
    Long id, String username, String role, String email, String firstname, Boolean enabled) {
}
