package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserCreateDto> getUsers() {
        return userService.getUsers()
                .stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserCreateDto getUserById(@PathVariable Long id) {
        return userMapper.mapToUserDto(userService.getUserById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreateDto createUser(@Valid @RequestBody UserCreateDto userDto) {
        User user = userMapper.mapToUser(userDto);
        return userMapper.mapToUserDto(userService.createUser(user));
    }

    @PatchMapping("/{id}")
    public UserCreateDto updateUser(@Valid @RequestBody UserUpdateDto userDto,
                                    @PathVariable Long id) {
        return userMapper.mapToUserDto(userService.updateUser(userDto, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
