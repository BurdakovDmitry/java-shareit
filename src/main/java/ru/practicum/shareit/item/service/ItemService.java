package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    List<ItemDto> getItemsByUser(Long userId);

    ItemDataDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    void deleteItem(Long itemId);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);
}
