package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        User user = findUserById(userId);

        ItemRequest request = requestMapper.mapToItemRequest(requestDto);
        request.setRequestor(user);

        ItemRequest newRequest = requestRepository.save(request);

        log.info("Добавлен новый запрос на вещь: {}", newRequest);

        return requestMapper.mapToItemRequestDto(newRequest);
    }

    @Override
    public List<ItemRequestGetDto> getItemRequestByUser(Long userId) {
        User user = findUserById(userId);

        List<ItemRequestGetDto> requests = requestRepository.findAllByRequestorOrderByCreatedDesc(user)
                .stream()
                .map(requestMapper::mapToItemRequestGetDto)
                .toList();

        log.info("Получен список запросов пользователя с ответами: {}", requests);

        return requests;
    }

    @Override
    public List<ItemRequestGetDto> getAllItemRequest(Long userId) {
        User user = findUserById(userId);

        List<ItemRequestGetDto> requests = requestRepository.findAllByRequestorNotOrderByCreatedDesc(user)
                .stream()
                .map(requestMapper::mapToItemRequestGetDto)
                .toList();

        log.info("Получен список всех запросов: {}", requests);

        return requests;
    }

    @Override
    public ItemRequestGetDto getItemRequestById(Long userId, Long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));

        log.info("Получен запрос: {}", request);

        return requestMapper.mapToItemRequestGetDto(request);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
