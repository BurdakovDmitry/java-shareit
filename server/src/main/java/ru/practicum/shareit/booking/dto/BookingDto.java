package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public record BookingDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        LocalDateTime start,

        LocalDateTime end,

        Long itemId,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        ItemDto item,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UserDto booker,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        BookingStatus status) {
}
