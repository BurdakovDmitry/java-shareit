package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        @Email(message = "Некорректный формат email")
        String email,
        String name) {
}
