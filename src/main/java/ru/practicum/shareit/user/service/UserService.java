package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(UserUpdateDto userDto, Long userId);

    User getUserById(Long userId);

    void deleteUser(Long userId);
}
