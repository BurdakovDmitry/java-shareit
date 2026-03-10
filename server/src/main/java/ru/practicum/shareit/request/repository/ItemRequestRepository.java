package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(User requestor);
}
