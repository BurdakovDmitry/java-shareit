package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.itemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingDto.itemId() + " не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " не доступна для бронирования");
        }

        if (userId.equals(item.getOwner().getId())) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        if (bookingDto.start().isAfter(bookingDto.end()) || bookingDto.start().equals(bookingDto.end())) {
            throw new ValidationException("Начало бронирование не может быть равна или позже конца бронирования");
        }

        Booking booking = bookingMapper.mapToBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking newBooking = bookingRepository.save(booking);

        log.info("Добавлен новый запрос: {}", newBooking);

        return bookingMapper.mapToBookingDto(newBooking);
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking oldBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (oldBooking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }

        if (!userId.equals(oldBooking.getItem().getOwner().getId())) {
            throw new ValidationException("Только владелец может менять статус");
        }

        if (approved == true) {
            oldBooking.setStatus(BookingStatus.APPROVED);
        } else {
            oldBooking.setStatus(BookingStatus.REJECTED);
        }

        Booking updateBooking = bookingRepository.save(oldBooking);

        log.info("Обновлен статус запроса: {}", updateBooking);

        return bookingMapper.mapToBookingDto(updateBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ValidationException("Только владелец или создатель брони могут делать запрос данных");
        }

        log.info("Получены данные брони: {}", booking);

        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByBroker(Long userId, BookingStatus state) {
        LocalDateTime presentTime = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(user);

        return switch (state) {
            case ALL -> bookings.stream()
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case CURRENT -> bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(presentTime)
                            && booking.getEnd().isAfter(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case PAST -> bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case FUTURE -> bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case WAITING -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case REJECTED -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            default -> throw new ValidationException("Некорректно переданный параметр state " + state);
        };
    }

    @Override
    public List<BookingDto> getBookingByOwner(Long userId, BookingStatus state) {
        LocalDateTime presentTime = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(user);

        if (bookings.isEmpty()) {
            return List.of();
        }

        return switch (state) {
            case ALL -> bookings.stream()
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case CURRENT -> bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(presentTime)
                            && booking.getEnd().isAfter(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case PAST -> bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case FUTURE -> bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(presentTime))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case WAITING -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            case REJECTED -> bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(bookingMapper::mapToBookingDto)
                    .toList();
            default -> throw new ValidationException("Некорректно переданный параметр state " + state);
        };
    }
}
