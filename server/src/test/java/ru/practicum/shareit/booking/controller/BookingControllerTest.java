package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;
    private final Long itemId = 3L;
    private final Long bookingId = 5L;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemId, null, null, BookingStatus.WAITING);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void updateItem() throws Exception {
        when(bookingService.updateBooking(userId, bookingId, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/5")
                        .header(userIdHeader, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/5")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getBookingByBroker() throws Exception {
        when(bookingService.getBookingByBroker(userId, BookingStatus.ALL)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.id()), Long.class));
    }

    @Test
    void getBookingByOwner() throws Exception {
        when(bookingService.getBookingByOwner(userId, BookingStatus.CURRENT)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(userIdHeader, userId)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.id()), Long.class));
    }
}
