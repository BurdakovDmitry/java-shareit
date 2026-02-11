package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;

public record BookingDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @NotNull(message = "Дата начала бронирования не может быть пустой")
        @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
        Instant start,

        @NotNull(message = "Дата окончания бронирования не может быть пустой")
        @Future(message = "Дата окончания бронирования должна быть в будущем")
        Instant end,

        @NotNull(message = "Объект бронирования должен быть указан")
        ItemDto item,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UserDto booker,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        BookingStatus status) {
}
