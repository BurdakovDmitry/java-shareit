package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;

public record ItemRequestDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank(message = "Описание запроса не может быть пустым")
        String description,

        @NotNull(message = "Пользователь, делающий запрос, обязателен")
        UserDto requestor,

        @NotNull(message = "Время создания запроса не может быть пустым")
        @PastOrPresent(message = "Время создания запроса не может быть в будущем")
        Instant created) {
}
