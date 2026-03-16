package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    private UserDto userDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(userId,"test@test.com", "User");
    }

    @Test
    void createUser() throws Exception {
        when(userClient.createUser(any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).createUser(any());
    }

    @Test
    void getUsers() throws Exception {
        when(userClient.getUsers()).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUsers();
    }

    @Test
    void getUserById() throws Exception {
        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateUser = new UserDto(userId,"newTest@test.com", "newUser");

        when(userClient.updateUser(any(), anyLong())).thenReturn(ResponseEntity.ok(updateUser));

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(any(), anyLong());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }

    @Test
    void createUserInvalidEmail() throws Exception {
        UserDto invalidUser = new UserDto(userId,"email", "newUser");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUserInvalidName() throws Exception {
        UserDto invalidUser = new UserDto(userId,"test@test.com", " ");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }
}