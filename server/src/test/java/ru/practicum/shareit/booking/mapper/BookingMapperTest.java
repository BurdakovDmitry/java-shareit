package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {
    private final BookingMapper mapper = new BookingMapperImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "userMapper", new UserMapperImpl());
        ReflectionTestUtils.setField(mapper, "itemMapper", new ItemMapperImpl());
    }

    @Test
    void mapToBookingDto() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("User");
        booker.setEmail("user@mail.com");

        Item item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Des");
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(5L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto = mapper.mapToBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.id());
        assertEquals(item.getId(), dto.itemId());
        assertEquals(booking.getStatus(), dto.status());
    }

    @Test
    void mapToBooking() {
        BookingDto dto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                2L, null, null, null);

        Booking entity = mapper.mapToBooking(dto);

        assertNull(entity.getId());
        assertNull(entity.getBooker());
    }
}