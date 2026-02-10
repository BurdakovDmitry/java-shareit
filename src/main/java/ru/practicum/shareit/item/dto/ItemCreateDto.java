package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public record ItemCreateDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotBlank(message = "Название не может быть пустым")
        String name,

        @NotBlank(message = "Описание не может быть пустым")
        String description,

        @NotNull(message = "Статус доступности обязателен")
        Boolean available,

        ItemRequestDto request){
}
