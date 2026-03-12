package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;
    private final Long itemId = 2L;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(itemId, "Item", "Description", null, true, List.of());
    }

    @Test
    void getItemsByUser() throws Exception {
        when(itemService.getItemsByUser(userId)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void getItemById() throws Exception {
        ItemDataDto itemDataDto = new ItemDataDto(itemId, "ItemData", "Description", true,
                null, null, List.of());

        when(itemService.getItemById(userId, itemId)).thenReturn(itemDataDto);

        mockMvc.perform(get("/items/2")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDataDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDataDto.getName())));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem("Item")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void createComment() throws Exception {
        CommentDto inputComment = new CommentDto(null, "Comment", null, null);
        CommentDto outputComment = new CommentDto(1L, "Comment", "User", LocalDateTime.now());

        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(outputComment);

        mockMvc.perform(post("/items/2/comment")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(inputComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is(outputComment.text())))
                .andExpect(jsonPath("$.authorName", is(outputComment.authorName())));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateItem = new ItemDto();
        updateItem.setName("updateItem");

        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(updateItem);

        mockMvc.perform(patch("/items/2")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateItem.getName())));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/2"))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItem(itemId);
    }
}