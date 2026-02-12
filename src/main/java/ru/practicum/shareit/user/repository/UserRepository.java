package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long userId);

    Optional<User> getUserByEmail(String email);

    void deleteUser(Long userId);
}
