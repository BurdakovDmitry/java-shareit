package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDataDto;
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

    private Booking booking;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "userMapper", new UserMapperImpl());
        ReflectionTestUtils.setField(mapper, "itemMapper", new ItemMapperImpl());

        booking = new Booking();
        booking.setId(5L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
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

        booking.setItem(item);
        booking.setBooker(booker);

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

    @Test
    void mapToBookingDataDto() {
        User booker = new User();
        booker.setId(5L);

        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDataDto dataDto = mapper.mapToBookingDataDto(booking);

        assertNotNull(dataDto);
        assertEquals(5L, dataDto.getBookerId());
    }

    @Test
    void mapToBookingDtoItemIsNull() {
        booking.setItem(null);

        BookingDto dto = mapper.mapToBookingDto(booking);

        assertNotNull(dto);
        assertNull(dto.itemId());
    }

    @Test
    void mapToBookingDataDtoBookerIsNull() {
        booking.setBooker(null);

        BookingDataDto dataDto = mapper.mapToBookingDataDto(booking);

        assertNotNull(dataDto);
        assertNull(dataDto.getBookerId());
    }

    @Test
    void mapToBookingDtoIsNull() {
        assertNull(mapper.mapToBooking(null));
        assertNull(mapper.mapToBookingDto(null));
        assertNull(mapper.mapToBookingDataDto(null));
    }

    @Test
    void mapToBookingDtoAll() {
        Item item = new Item();
        item.setId(5L);

        User booker = new User();
        booker.setId(7L);

        booking.setItem(item);
        booking.setBooker(booker);

        BookingDto dto = mapper.mapToBookingDto(booking);

        assertNotNull(dto);
        assertEquals(5L, dto.itemId());
    }
}