package ru.practicum.shareit.item.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemCreateDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUser(userId)
                .stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @GetMapping("/{itemId}")
    public ItemCreateDto getItemById(@PathVariable Long itemId) {
        return itemMapper.mapToItemDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    public List<ItemCreateDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text)
                .stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemCreateDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Valid @RequestBody ItemCreateDto itemDto) {
        Item item = itemMapper.mapToItem(itemDto);
        return itemMapper.mapToItemDto(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemCreateDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody ItemUpdateDto itemDto,
                                    @PathVariable Long itemId) {
        return itemMapper.mapToItemDto(itemService.updateItem(userId, itemDto, itemId));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
