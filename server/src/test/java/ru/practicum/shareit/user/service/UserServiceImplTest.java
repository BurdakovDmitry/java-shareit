package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user = new User();
    private UserDto userDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail("test@test.com");
        user.setName("User");

        userDto = new UserDto(userId, "test@test.com", "User");
    }

    @Test
    void getUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        List<UserDto> users = userService.getUsers();

        assertEquals(1, users.size());
        assertEquals(userDto, users.getFirst());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto getUserDto = userService.getUserById(userId);

        assertNotNull(getUserDto);
        assertEquals(userDto.email(), getUserDto.email());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser() {
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
        when(userMapper.mapToUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDto saved = userService.createUser(userDto);

        assertNotNull(saved);
        assertEquals("test@test.com", saved.email());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUserDuplicateEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(DuplicatedDataException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser() {
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setEmail("newTest@test.com");
        updateUser.setName("newUser");

        UserDto updateUserDto = new UserDto(userId, "newTest@test.com", "newUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("newTest@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updateUser);
        when(userMapper.mapToUserDto(any(User.class))).thenReturn(updateUserDto);

        UserDto result = userService.updateUser(updateUserDto, userId);

        assertNotNull(result);
        assertEquals("newTest@test.com", result.email());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserInvalidEmail() {
        UserDto invalidUser = new UserDto(null, "invalidEmail", "Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userService.updateUser(invalidUser, userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}