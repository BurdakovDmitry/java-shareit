package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient requestClient;

    private ItemRequestDto requestDto;
    private ItemRequestGetDto requestGetDto;
    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long requestId = 5L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto(requestId,"Description", null, LocalDateTime.now());
        requestGetDto =
                new ItemRequestGetDto(requestId, "Description", null, LocalDateTime.now(), List.of());
    }

    @Test
    void create() throws Exception {
        when(requestClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).create(anyLong(), any());
    }

    @Test
    void createInvalidDescription() throws Exception {
        ItemRequestDto invalidDto = new ItemRequestDto(requestId," ", null, LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).create(anyLong(), any());
    }

    @Test
    void getItemRequestByUser() throws Exception {
        when(requestClient.getItemRequestByUser(userId)).thenReturn(ResponseEntity.ok(requestGetDto));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getItemRequestByUser(userId);
    }

    @Test
    void getAllItemRequest() throws Exception {
        when(requestClient.getAllItemRequest(userId)).thenReturn(ResponseEntity.ok(requestGetDto));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getAllItemRequest(userId);
    }

    @Test
    void getItemRequestById() throws Exception {
        when(requestClient.getItemRequestById(userId, requestId)).thenReturn(ResponseEntity.ok(requestGetDto));

        mockMvc.perform(get("/requests/5")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getItemRequestById(userId, requestId);
    }
}