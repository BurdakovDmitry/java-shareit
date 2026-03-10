package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private final Long ownerId = 1L;
    private final Long itemId = 3L;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");

        item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setOwner(owner);

        itemDto = new ItemDto(itemId, "Item", "Description", null, true, List.of());
    }

    @Test
    void getItemsByUser() {
        LocalDateTime now = LocalDateTime.now();
        Booking last = new Booking();
        last.setStart(now.minusDays(1));

        Booking next = new Booking();
        next.setStart(now.plusDays(1));

        ItemDataDto itemDataDto = new ItemDataDto();
        itemDataDto.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItemDataDto(item)).thenReturn(itemDataDto);
        when(bookingRepository.findAllByItemIdInAndStatusNot(anyList(), any())).thenReturn(List.of(last, next));

        ItemDataDto getItem = itemService.getItemById(ownerId, itemId);

        assertNotNull(getItem);
        verify(bookingMapper, times(2)).mapToBookingDataDto(any());
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatusNot(anyList(), any());
    }

    @Test
    void getItemById() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwner(owner)).thenReturn(List.of(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        List<ItemDto> items = itemService.getItemsByUser(ownerId);

        assertEquals(1, items.size());
        assertEquals(itemDto, items.getFirst());
        verify(itemRepository, times(1)).findByOwner(owner);
    }

    @Test
    void searchItem() {
        when(itemRepository.search("Item")).thenReturn(List.of(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        List<ItemDto> items = itemService.searchItem("Item");

        assertEquals(1, items.size());
        assertEquals(itemDto, items.getFirst());
        verify(itemRepository, times(1)).search("Item");
    }

    @Test
    void searchItemTextEmpty() {
        List<ItemDto> items = itemService.searchItem(" ");

        assertTrue(items.isEmpty());
        verify(itemRepository, never()).search(" ");
    }

    @Test
    void createItem() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);

        ItemDto save = itemService.createItem(ownerId, itemDto);

        assertNotNull(save);
        assertEquals(itemDto.getId(), save.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItem() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("NewItem");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        itemService.updateItem(ownerId, updateDto, itemId);

        verify(itemMapper).updateMapToItem(updateDto, item);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemNotOwner() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("NewItem");

        assertThrows(NotFoundException.class, () -> itemService.updateItem(99L, updateDto, itemId));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void deleteItem() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto(null, "Comment", null, null);
        Comment comment = new Comment();
        comment.setText("Comment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(commentMapper.mapToComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        itemService.addComment(commentDto, ownerId, itemId);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addCommentValidationException() {
        CommentDto commentDto = new CommentDto(null, "Text", null, null);
        Comment comment = new Comment();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThrows(ValidationException.class, () ->
                itemService.addComment(commentDto, ownerId, itemId));

        verify(commentRepository, never()).save(comment);
    }
}