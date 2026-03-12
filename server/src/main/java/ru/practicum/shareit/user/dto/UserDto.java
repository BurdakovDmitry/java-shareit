package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,
        String email,
        String name) {
}
