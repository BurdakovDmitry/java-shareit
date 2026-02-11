package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    List<ItemDto> getItemsByUser(Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    void deleteItem(Long itemId);
}
