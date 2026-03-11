package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long itemId = 3L;
    private final Long bookingId = 5L;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(userId);
        booker.setName("Booker");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");

        item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        bookingDto = new BookingDto(bookingId, booking.getStart(), booking.getEnd(),
                itemId, null, null, BookingStatus.WAITING);
    }

    @Test
    void create() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingMapper.mapToBooking(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto saved = bookingService.create(userId, bookingDto);

        assertNotNull(saved);
        assertEquals(bookingId, saved.id());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createInvalidAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.create(userId, bookingDto));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createInvalidOwner() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> bookingService.create(ownerId, bookingDto));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createInvalidDataTime() {
        BookingDto invalidBookingDto = new BookingDto(bookingId, booking.getEnd(), booking.getEnd(),
                itemId, null, null, BookingStatus.WAITING);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.create(userId, invalidBookingDto));
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void updateBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        bookingService.updateBooking(ownerId, bookingId, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingRejectedStatus() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        bookingService.updateBooking(ownerId, bookingId, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateNotWaitingStatus() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBooking(userId, bookingId, true));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateNotOwner() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBooking(userId, bookingId, true));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto getBookingDto = bookingService.getBookingById(userId, bookingId);

        assertNotNull(getBookingDto);
        assertEquals(bookingId, getBookingDto.id());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdOwner() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto getBookingDto = bookingService.getBookingById(ownerId, bookingId);

        assertNotNull(getBookingDto);
        assertEquals(bookingId, getBookingDto.id());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdNotOwner() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(99L, bookingId));
    }

    @Test
    void getBookingByBrokerStateFuture() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerStateAll() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.ALL);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerStateCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerStatePast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.PAST);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerStateWaiting() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerStateRejected() {
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByBroker(userId, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByBookerOrderByStartDesc(booker);
    }

    @Test
    void getBookingByBrokerInvalidState() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerOrderByStartDesc(booker)).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.getBookingByBroker(userId, BookingStatus.APPROVED));
    }

    @Test
    void getBookingByOwner() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.FUTURE);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerStateAll() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.ALL);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerStateCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.CURRENT);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerStatePast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.PAST);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerStateWaiting() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerStateRejected() {
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));
        when(bookingMapper.mapToBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getBookingByOwner(ownerId, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.getFirst());
        verify(bookingRepository, times(1)).findAllByItemOwnerOrderByStartDesc(owner);
    }

    @Test
    void getBookingByOwnerInvalidState() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(owner)).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.getBookingByOwner(ownerId, BookingStatus.APPROVED));
    }
}