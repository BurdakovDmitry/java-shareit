package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
}