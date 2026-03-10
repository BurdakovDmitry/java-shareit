package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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
    private ItemClient itemClient;

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
        when(itemClient.getItemsByUser(userId)).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItemsByUser(userId);
    }

    @Test
    void getItemById() throws Exception {
        ItemDataDto itemDataDto = new ItemDataDto(itemId, "ItemData", "Description", true,
                null, null, List.of());

        when(itemClient.getItemById(userId, itemId)).thenReturn(ResponseEntity.ok(itemDataDto));

        mockMvc.perform(get("/items/2")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDataDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDataDto.getName())));

        verify(itemClient, times(1)).getItemById(userId, itemId);
    }

    @Test
    void searchItem() throws Exception {
        when(itemClient.searchItem("Item")).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).searchItem("Item");
    }

    @Test
    void createItem() throws Exception {
        when(itemClient.createItem(anyLong(), any())).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(anyLong(), any());
    }

    @Test
    void createItemInvalidName() throws Exception {
        ItemDto invalidItemDto =
                new ItemDto(itemId," ", "Description", null, true, List.of());

        when(itemClient.createItem(anyLong(), any())).thenReturn(ResponseEntity.ok(invalidItemDto));

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    void createComment() throws Exception {
        CommentDto inputComment = new CommentDto(null, "Comment", null, null);
        CommentDto outputComment = new CommentDto(55L,"Comment", "User", LocalDateTime.now());

        when(itemClient.addComment(any(), anyLong(), anyLong())).thenReturn(ResponseEntity.ok(outputComment));

        mockMvc.perform(post("/items/2/comment")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(inputComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(any(), anyLong(), anyLong());
    }

    @Test
    void createCommentInvalidText() throws Exception {
        CommentDto invalidComment = new CommentDto(null," ", null, null);

        mockMvc.perform(post("/items/2/comment")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(any(), anyLong(), anyLong());
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateItem = new ItemDto();
        updateItem.setName("updateItem");

        when(itemClient.updateItem(anyLong(), any(), anyLong())).thenReturn(ResponseEntity.ok(updateItem));

        mockMvc.perform(patch("/items/2")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(anyLong(), any(), anyLong());
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/2"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).deleteItem(itemId);
    }
}