package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {
    @Autowired
    private ItemRequestClient client;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto =
                new ItemRequestDto(null, "Des", null, null);

        String jsonResponse = mapper.writeValueAsString(itemRequestDto);

        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(userIdHeader, userId.toString()))
                .andExpect(content().json(jsonResponse))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.create(userId, itemRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getItemRequestByUser() {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.getItemRequestByUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getAllItemRequest() {
        mockServer.expect(requestTo("http://localhost:9090/requests/all"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.getAllItemRequest(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getItemRequestById() {
        Long requestId = 2L;

        mockServer.expect(requestTo("http://localhost:9090/requests/" + requestId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.getItemRequestById(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}
