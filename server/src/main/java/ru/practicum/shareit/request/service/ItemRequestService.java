package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto requestDto);

    List<ItemRequestGetDto> getItemRequestByUser(Long userId);

    List<ItemRequestGetDto> getAllItemRequest(Long userId);

    ItemRequestGetDto getItemRequestById(Long userId, Long requestId);
}
