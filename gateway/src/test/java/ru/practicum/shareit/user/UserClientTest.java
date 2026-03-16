package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
class UserClientTest {
    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    private final Long userId = 1L;

    @Test
    void getUsers() {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getUserById() {
        mockServer.expect(requestTo("http://localhost:9090/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto(userId, "user@mail.com", "User");

        String jsonRequest = mapper.writeValueAsString(userDto);

        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(jsonRequest))
                .andRespond(withSuccess(jsonRequest, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.createUser(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateDto = new UserDto(userId, "new@mail.com", "newUser");

        String jsonRequest = mapper.writeValueAsString(updateDto);

        mockServer.expect(requestTo("http://localhost:9090/users/" + userId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json(jsonRequest))
                .andRespond(withSuccess(jsonRequest, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.updateUser(updateDto, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void deleteUser() {
        mockServer.expect(requestTo("http://localhost:9090/users/" + userId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}