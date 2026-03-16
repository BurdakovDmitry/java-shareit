package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public record ItemRequestDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        String description,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UserDto requestor,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime created) {
}
