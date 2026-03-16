package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {
    private final ItemMapper mapper = new ItemMapperImpl();

    private ItemDto dto;
    private Item item;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        Long ownerId = 2L;

        User owner = new User();
        owner.setId(2L);

        dto = new ItemDto(itemId, "Item", "Des", ownerId, true, List.of());

        item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setDescription("Des");
        item.setAvailable(true);
        item.setOwner(owner);

        ReflectionTestUtils.setField(mapper, "commentMapper", new CommentMapperImpl());
    }

    @Test
    void mapToItem() {
        Item item = mapper.mapToItem(dto);

        assertNotNull(item);
        assertEquals("Item", item.getName());
        assertTrue(item.getAvailable());
        assertNotNull(item.getRequest());
        assertNull(item.getId());
    }

    @Test
    void updateMapToItem() {
        ItemDto updateDto =
                new ItemDto(itemId, "newItem", null, null, null, List.of());

        mapper.updateMapToItem(updateDto, item);

        assertEquals(1L, item.getId()); // ID не изменился
        assertEquals("newItem", item.getName());
        assertEquals("Des", item.getDescription());
    }

    @Test
    void mapToItemAnswerDto() {
        ItemAnswerDto dto = mapper.mapToItemAnswerDto(item);

        assertEquals(2L, dto.ownerId());
    }

    @Test
    void requestNull() {
        ItemRequest request = mapper.request(null);

        assertNull(request);
    }

    @Test
    void mapToItemDto() {
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        item.setRequest(request);

        ItemDto resultDto = mapper.mapToItemDto(item);

        assertNotNull(resultDto);
        assertEquals(itemId, resultDto.getId());
        assertEquals(5L, resultDto.getRequestId());
    }

    @Test
    void mapToItemDataDto() {
        ItemDataDto dataDto = mapper.mapToItemDataDto(item);

        assertNotNull(dataDto);
        assertEquals("Item", dataDto.getName());
        assertNull(dataDto.getLastBooking());
        assertNull(dataDto.getNextBooking());
    }

    @Test
    void mapToItemNull() {
        assertNull(mapper.mapToItem(null));
        assertNull(mapper.mapToItemDto(null));
        assertNull(mapper.mapToItemDataDto(null));
        assertNull(mapper.mapToItemAnswerDto(null));
    }

    @Test
    void updateMapToItemDtoIsNull() {
        String oldName = item.getName();
        mapper.updateMapToItem(null, item);
        assertEquals(oldName, item.getName());
    }

    @Test
    void requestValidId() {
        Long requestId = 5L;
        ItemRequest result = mapper.request(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void mapToItemDataDtoComments() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        item.setComments(List.of(comment));

        ItemDataDto result = mapper.mapToItemDataDto(item);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals("Comment", result.getComments().getFirst().text());
    }

    @Test
    void mapToItemDtoRequestIsNull() {
        item.setRequest(null);

        ItemDto dto = mapper.mapToItemDto(item);

        assertNull(dto.getRequestId());
    }

    @Test
    void mapToItemAnswerDtoOwnerIsNull() {
        item.setOwner(null);

        ItemAnswerDto answerDto = mapper.mapToItemAnswerDto(item);

        assertNotNull(answerDto);
        assertNull(answerDto.ownerId());
    }

    @Test
    void mapToItemDataDtoCommentsNull() {
        item.setComments(null);

        ItemDataDto dataDto = mapper.mapToItemDataDto(item);

        assertNotNull(dataDto);
        assertNull(dataDto.getComments());
    }
}