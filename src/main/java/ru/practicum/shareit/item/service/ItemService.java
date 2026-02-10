package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemService {
    Collection<Item> getItemsByUser(Long userId);

    Item getItemById(Long itemId);

    Collection<Item> searchItem(String text);

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, ItemUpdateDto itemDto, Long itemId);

    void deleteItem(Long itemId);
}
