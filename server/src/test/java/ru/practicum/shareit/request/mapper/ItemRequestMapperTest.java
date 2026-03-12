package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapperImpl;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapperImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "userMapper", new UserMapperImpl());
    }

    @Test
    void mapToItemRequest() {
        ItemRequestDto dto = new ItemRequestDto(1L, "Des", null, null);

        ItemRequest request = mapper.mapToItemRequest(dto);

        assertNotNull(request);
        assertEquals("Des", request.getDescription());
        assertNull(request.getId());
        assertNull(request.getRequestor());
        assertNull(request.getCreated());
        assertNull(request.getItems());
    }

    @Test
    void mapToItemRequestDto() {
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        request.setDescription("Des");
        request.setCreated(LocalDateTime.now());

        ItemRequestDto dto = mapper.mapToItemRequestDto(request);

        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("Des", dto.description());
        assertNotNull(dto.created());
    }

    @Test
    void mapToItemRequestGetDto() {
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        request.setDescription("Des");
        request.setItems(Collections.emptyList());

        ItemRequestGetDto getDto = mapper.mapToItemRequestGetDto(request);

        assertNotNull(getDto);
        assertEquals(5L, getDto.getId());
        assertNotNull(getDto.getItems());
        assertTrue(getDto.getItems().isEmpty());
    }
}