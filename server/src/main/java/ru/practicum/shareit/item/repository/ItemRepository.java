package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner);

    @Query("""
            SELECT it
            FROM Item AS it
            WHERE it.available = true
            AND (UPPER(it.name) LIKE UPPER(CONCAT('%', ?1, '%'))
            OR UPPER(it.description) LIKE UPPER(CONCAT('%', ?1, '%')))
            """)
    List<Item> search(String text);
}
