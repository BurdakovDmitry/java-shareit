package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CommentDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank(message = "Отзыв не может быть пустым")
        String text,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String authorName,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime created) {
}
