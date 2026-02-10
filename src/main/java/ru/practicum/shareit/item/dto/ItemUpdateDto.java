package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public record ItemUpdateDto(
        String name,
        String description,
        Boolean available,
        ItemRequestDto request) {
}
