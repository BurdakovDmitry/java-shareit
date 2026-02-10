package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class Validation {
    private final UserRepository userRepository;

    public void userById(Long id) {
        if (userRepository.getUserById(id).isEmpty()) {
            log.warn("Пользователь с id = {} в базе данных не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public void uniqueEmailCreateUser(String email) {
        if (userRepository.getUserByEmail(email).isPresent()) {
            log.warn("Попытка регистрации с уже существующим email: {}", email);
            throw new DuplicatedDataException("Данный email уже используется");
        }
    }

    public void uniqueEmailUpdateUser(String email, Long userId) {
        userRepository.getUserByEmail(email)
            .filter(user -> !user.getId().equals(userId))
            .ifPresent(user -> {
                log.warn("Email {} уже занят другим пользователем", email);
                throw new DuplicatedDataException("Данный email уже используется");
            });
    }
}
