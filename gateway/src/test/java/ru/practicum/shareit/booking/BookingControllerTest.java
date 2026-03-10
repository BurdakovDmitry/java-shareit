package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

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
        when(bookingClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).create(anyLong(), any());
    }

    @Test
    void createInvalidData() throws Exception {
        BookingDto invalidBookingDto = new BookingDto(bookingId, null, LocalDateTime.now().plusDays(1),
                itemId, null, null, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void createInvalidItem() throws Exception {
        BookingDto invalidBookingDto = new BookingDto(bookingId, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), null, null, null, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        when(bookingClient.updateBooking(userId, bookingId, true)).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(patch("/bookings/5")
                        .header(userIdHeader, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).updateBooking(userId, bookingId, true);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingClient.getBookingById(userId, bookingId)).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings/5")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingById(userId, bookingId);
    }

    @Test
    void getBookingByBroker() throws Exception {
        when(bookingClient.getBookingByBroker(userId, BookingStatus.ALL)).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingByBroker(userId, BookingStatus.ALL);
    }

    @Test
    void getBookingByOwner() throws Exception {
        when(bookingClient.getBookingByOwner(userId, BookingStatus.CURRENT)).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(userIdHeader, userId)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingByOwner(userId, BookingStatus.CURRENT);
    }
}
