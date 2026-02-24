package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        log.info("Получен пользователь с id = {}", userId);

        return userMapper.mapToUserDto(user);
    }

    @Override
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
    public UserDto updateUser(UserDto userDto, Long userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (userDto.email() != null && !userDto.email().contains("@")) {
            log.warn("Валидация по email не пройдена - некорректный формат email");
            throw new ValidationException("Некорректный формат email");
        }

        userMapper.updateMapToUser(userDto, oldUser);

        userRepository.findByEmail(oldUser.getEmail())
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    log.warn("Email {} уже занят другим пользователем", oldUser.getEmail());
                    throw new DuplicatedDataException("Данный email уже используется");
                });

        User updateUser = userRepository.save(oldUser);

        log.info("Обновлены данные пользователя: {}", updateUser);

        return userMapper.mapToUserDto(updateUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        userRepository.deleteById(userId);

        log.info("Пользователь с id = {} спешно удален", userId);
    }
}
