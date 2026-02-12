package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class ItemRepositoryImp implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<Item> getItemsByUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> search(String text) {
        String query = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(query)
                        || item.getDescription() != null && item.getDescription().toLowerCase().contains(query))
                .toList();
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    private Long getNextId() {
        return id++;
    }
}
