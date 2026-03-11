package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemClient.class)
class ItemClientTest {
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;
    private final Long itemId = 2L;

    @Test
    void createItem() throws  Exception {
        ItemDto itemDto = new ItemDto(itemId, "Item", "Des", 5L, null, List.of());

        String jsonResponse = mapper.writeValueAsString(itemDto);

        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(userIdHeader, userId.toString()))
                .andExpect(content().json(jsonResponse))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.createItem(userId, itemDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Comment", null, null);

        String jsonResponse = mapper.writeValueAsString(commentDto);

        mockServer.expect(requestTo("http://localhost:9090/items/2/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(userIdHeader, userId.toString()))
                .andExpect(content().json(jsonResponse))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.addComment(commentDto, userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void searchItem() {
        String searchText = "Text";

        mockServer.expect(requestToUriTemplate("http://localhost:9090/items/search?text=" + searchText))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.searchItem(searchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void searchItemItIsBlank() {
        ResponseEntity<Object> response = itemClient.searchItem("   ");

        mockServer.verify();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getItemsByUser() {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemsByUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getItemById() {
        mockServer.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(userIdHeader, userId.toString()))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItemById(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateDto =
                new ItemDto(null, "NewItem", null, null, null, List.of());

        String jsonResponse = mapper.writeValueAsString(updateDto);

        mockServer.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(userIdHeader, userId.toString()))
                .andExpect(content().json(jsonResponse))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.updateItem(userId, updateDto, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void deleteItem() {
        mockServer.expect(requestTo("http://localhost:9090/items/" + itemId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = itemClient.deleteItem(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}