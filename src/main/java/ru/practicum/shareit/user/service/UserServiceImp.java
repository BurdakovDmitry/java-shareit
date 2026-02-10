package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.Validation;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final Validation validation;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<User> getUsers() {
        Collection<User> users = userRepository.getUsers();

        log.info("Получен список пользователей: {}", users);

        return users;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        log.info("Получен пользователь с id = {}", userId);

        return user;
    }

    @Override
    public User createUser(User user) {
        validation.uniqueEmailCreateUser(user.getEmail());
        User newUser = userRepository.createUser(user);

        log.info("Добавлен новый пользователь: {}", newUser);

        return newUser;
    }

    @Override
    public User updateUser(UserUpdateDto userDto, Long userId) {
        User oldUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userMapper.updateUserFromDto(userDto, oldUser);

        validation.uniqueEmailUpdateUser(oldUser.getEmail(), userId);

        User updateUser = userRepository.updateUser(oldUser);

        log.info("Обновлены данные пользователя: {}", updateUser);

        return updateUser;
    }

    @Override
    public void deleteUser(Long userId) {
        validation.userById(userId);
        userRepository.deleteUser(userId);

        log.info("Пользователь с id = {} спешно удален", userId);
    }
}
