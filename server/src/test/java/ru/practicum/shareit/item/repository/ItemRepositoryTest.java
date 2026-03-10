package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Item newItem = new Item();
        newItem.setName("newItem");
        newItem.setDescription("newDescription");
        newItem.setAvailable(false);
        newItem.setOwner(user);
        itemRepository.save(newItem);
    }

    @Test
    void findByOwner() {
        List<Item> items = itemRepository.findByOwner(user);

        assertEquals(2, items.size());
        assertTrue(items.contains(item));
    }

    @Test
    void searchWhenName() {
        List<Item> items = itemRepository.search("ITEM");

        assertEquals(1, items.size());
        assertEquals("Item", items.getFirst().getName());
    }

    @Test
    void searchDescription() {
        List<Item> items = itemRepository.search("Desc");

        assertEquals(1, items.size());
        assertEquals("Description", items.getFirst().getDescription());
    }

    @Test
    void searchNotAvailable() {
        List<Item> items = itemRepository.search("newItem");

        assertTrue(items.isEmpty());
    }
}