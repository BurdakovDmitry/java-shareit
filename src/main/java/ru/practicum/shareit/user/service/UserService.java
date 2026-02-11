package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
