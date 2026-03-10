package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);


        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        bookingRepository.save(booking);
    }

    @Test
    void findAllByBookerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker);

        assertEquals(1, bookings.size());
        assertEquals(booker.getId(), bookings.getFirst().getBooker().getId());
    }

    @Test
    void findAllByItemOwnerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner);

        assertEquals(1, bookings.size());
        assertEquals(owner.getId(), bookings.getFirst().getItem().getOwner().getId());
    }

    @Test
    void findAllByItemIdInAndStatusNot() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusNot(
                List.of(item.getId()), BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(booker.getId(), bookings.getFirst().getBooker().getId());
        assertNotEquals(BookingStatus.REJECTED, bookings.getFirst().getStatus());
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore_False() {
        boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(), booker.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertFalse(exists);
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore_True() {
        Booking approvedBooking = new Booking();
        approvedBooking.setItem(item);
        approvedBooking.setBooker(booker);
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setStart(LocalDateTime.now().minusDays(2));
        approvedBooking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(approvedBooking);

        boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(), booker.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertTrue(exists);
    }
}