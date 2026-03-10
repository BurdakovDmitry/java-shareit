package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDataDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDataDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDataDto lastBooking;
    private BookingDataDto nextBooking;
    private List<CommentDto> comments;
}
