package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    private ItemRequestDto requestDto;
    private ItemRequestGetDto requestGetDto;
    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long requestId = 5L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto(requestId, "Description", null, LocalDateTime.now());
        requestGetDto =
                new ItemRequestGetDto(requestId, "Description", null, LocalDateTime.now(), List.of());
    }

    @Test
    void create() throws Exception {
        when(requestService.create(anyLong(), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.id()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.description())));
    }

    @Test
    void getItemRequestByUser() throws Exception {
        when(requestService.getItemRequestByUser(userId)).thenReturn(List.of(requestGetDto));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestGetDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestGetDto.getDescription())))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getAllItemRequest() throws Exception {
        when(requestService.getAllItemRequest(userId)).thenReturn(List.of(requestGetDto));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestGetDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestGetDto.getDescription())))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(requestService.getItemRequestById(userId, requestId)).thenReturn(requestGetDto);

        mockMvc.perform(get("/requests/5")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestGetDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestGetDto.getDescription())))
                .andExpect(jsonPath("$.items").isArray());
    }
}