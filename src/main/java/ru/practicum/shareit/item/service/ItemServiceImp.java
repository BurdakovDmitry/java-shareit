package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.validation.Validation;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final Validation validation;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Collection<Item> getItemsByUser(Long userId) {
        validation.userById(userId);
        Collection<Item> items = itemRepository.getItemsByUser(userId);

        log.info("Получен список вещей пользователя с id = {}", userId);

        return items;
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        log.info("Получена вещь с id = {}", itemId);

        return item;
    }

    @Override
    public Collection<Item> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text);
    }

    @Override
    public Item createItem(Long userId, Item item) {
        validation.userById(userId);
        item.setOwner(userId);

        Item newItem = itemRepository.createItem(item);

        log.info("Добавлена новая вещь: {}", newItem);

        return newItem;
    }

    @Override
    public Item updateItem(Long userId, ItemUpdateDto itemDto, Long itemId) {
        Item oldItem = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!oldItem.getOwner().equals(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не является владельцем данной вещи.");
        }

        itemMapper.updateItemFromDto(itemDto, oldItem);

        Item updateItem = itemRepository.updateItem(oldItem);

        log.info("Обновлены данные вещи: {}", updateItem);

        return updateItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);

        log.info("Вещь с id = {} спешно удалена", itemId);
    }
}
