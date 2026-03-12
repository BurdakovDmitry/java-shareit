package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        Collection<User> users = userRepository.findAll();

        log.info("Получен список пользователей: {}", users);

        return users.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserById(userId);

        log.info("Получен пользователь с id = {}", userId);

        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.mapToUser(userDto);

        userRepository.findByEmail(userDto.email()).ifPresent(userByEmail -> {
            log.warn("Email {} уже используется", userDto.email());
            throw new DuplicatedDataException("Данный email уже используется");
        });

        User newUser = userRepository.save(user);

        log.info("Добавлен новый пользователь: {}", newUser);

        return userMapper.mapToUserDto(newUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User oldUser = findUserById(userId);

        if (userDto.email() != null && !userDto.email().contains("@")) {
            log.warn("Валидация по email не пройдена - некорректный формат email");
            throw new ValidationException("Некорректный формат email");
        }

        userRepository.findByEmail(userDto.email())
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    log.warn("Email {} уже занят другим пользователем", userDto.email());
                    throw new DuplicatedDataException("Данный email уже используется");
                });

        userMapper.updateMapToUser(userDto, oldUser);
        User updateUser = userRepository.save(oldUser);

        log.info("Обновлены данные пользователя: {}", updateUser);

        return userMapper.mapToUserDto(updateUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        findUserById(userId);

        userRepository.deleteById(userId);

        log.info("Пользователь с id = {} спешно удален", userId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
