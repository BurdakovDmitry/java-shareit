package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Collection<Item> items = itemRepository.getItemsByUser(userId);

        log.info("Получен список вещей пользователя с id = {}", userId);

        return items.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        log.info("Получена вещь с id = {}", itemId);

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(userId);

        Item newItem = itemRepository.createItem(item);

        log.info("Добавлена новая вещь: {}", newItem);

        return itemMapper.mapToItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item oldItem = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!oldItem.getOwner().equals(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не является владельцем данной вещи.");
        }

        itemMapper.updateMapToItem(itemDto, oldItem);

        Item updateItem = itemRepository.updateItem(oldItem);

        log.info("Обновлены данные вещи: {}", updateItem);

        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        itemRepository.deleteItem(itemId);

        log.info("Вещь с id = {} спешно удалена", itemId);
    }
}
