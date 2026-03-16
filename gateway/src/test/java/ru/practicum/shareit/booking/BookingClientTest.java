package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BookingClient.class)
class BookingClientTest {
    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;
    private final Long bookingId = 2L;

    @Test
    void createItem() throws Exception {
        BookingDto bookingDto = new BookingDto(bookingId, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 3L, null, null, null);

        String jsonResponse = mapper.writeValueAsString(bookingDto);

        mockServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(userIdHeader, userId.toString()))
                .andExpect(content().json(jsonResponse))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.create(userId, bookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void updateBooking() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/2?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = bookingClient.updateBooking(userId, bookingId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getBookingById() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/" + bookingId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingById(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getBookingByBroker() {
        mockServer.expect(requestTo("http://localhost:9090/bookings?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingByBroker(userId, BookingStatus.ALL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getBookingByOwner() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/owner?state=PAST"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingByOwner(userId, BookingStatus.PAST);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}
