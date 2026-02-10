package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserRepositoryImp implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> uniqueEmail = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(uniqueEmail.get(email));
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());

        log.info("Пользователю {} присвоился id={}.", user.getName(), user.getId());

        users.put(user.getId(), user);
        uniqueEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        uniqueEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Ошибка поиска пользователя c id = {}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        uniqueEmail.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    private Long getNextId() {
        return id++;
    }
}
