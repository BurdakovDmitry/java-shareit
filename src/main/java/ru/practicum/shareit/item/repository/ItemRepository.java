package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getItemsByUser(Long userId);

    Optional<Item> getItemById(Long itemId);

    Item createItem(Item item);

    Item updateItem(Item item);

    Collection<Item> search(String text);

    void deleteItem(Long itemId);
}
