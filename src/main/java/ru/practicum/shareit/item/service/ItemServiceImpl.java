package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Item> items = itemRepository.findByOwner(user);

        log.info("Получен список вещей пользователя с id = {}", userId);

        return items.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDataDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemDataDto dto = itemMapper.mapToItemDataDto(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime presetTime = LocalDateTime.now();

            List<Booking> bookings = bookingRepository
                    .findAllByItemIdInAndStatusNot(List.of(itemId), BookingStatus.REJECTED);

            dto.setLastBooking(bookings.stream()
                    .filter(b -> !b.getStart().isAfter(presetTime))
                    .max(Comparator.comparing(Booking::getStart))
                    .map(bookingMapper::mapToBookingDataDto)
                    .orElse(null));

            dto.setNextBooking(bookings.stream()
                    .filter(b -> b.getStart().isAfter(presetTime))
                    .min(Comparator.comparing(Booking::getStart))
                    .map(bookingMapper::mapToBookingDataDto)
                    .orElse(null));
        }

        log.info("Получена вещь с id = {}", itemId);

        return dto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(user);

        Item newItem = itemRepository.save(item);

        log.info("Добавлена новая вещь: {}", newItem);

        return itemMapper.mapToItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не является владельцем данной вещи.");
        }

        itemMapper.updateMapToItem(itemDto, oldItem);

        Item updateItem = itemRepository.save(oldItem);

        log.info("Обновлены данные вещи: {}", updateItem);

        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        itemRepository.deleteById(itemId);

        log.info("Вещь с id = {} спешно удалена", itemId);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        LocalDateTime presentTime = LocalDateTime.now();
        boolean isComment = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, presentTime);

        if (!isComment) {
            throw new ValidationException("Отзыв можно оставить после окончания брони и только если вы ее бронировали");
        }

        Comment comment = commentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthorName(user.getName());

        Comment newComment = commentRepository.save(comment);

        log.info("Добавлен новый комментарий: {}", newComment);

        return commentMapper.mapToCommentDto(newComment);
    }
}
