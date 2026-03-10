package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User newUser;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");

        newUser = new User();
        newUser.setName("newUser");
        newUser.setEmail("newuser@mail.com");

        ItemRequest request = new ItemRequest();
        request.setDescription("Request");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("newRequest");
        newRequest.setRequestor(newUser);
        newRequest.setCreated(LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(newUser);
        requestRepository.save(request);
        requestRepository.save(newRequest);
    }


    @Test
    void findAllByRequestorOrderByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorOrderByCreatedDesc(user);

        assertEquals(1, requests.size());
        assertEquals("Request", requests.getFirst().getDescription());
        assertEquals(user.getId(), requests.getFirst().getRequestor().getId());
    }

    @Test
    void findAllByRequestorNotOrderByCreatedDesc() {
        ItemRequest oldRequests = new ItemRequest();
        oldRequests.setDescription("oldRequests");
        oldRequests.setRequestor(newUser);
        oldRequests.setCreated(LocalDateTime.now().minusDays(2));

        requestRepository.save(oldRequests);

        List<ItemRequest> requests = requestRepository.findAllByRequestorNotOrderByCreatedDesc(user);

        assertEquals(2, requests.size());
        assertEquals("oldRequests", requests.getFirst().getDescription());
        assertTrue(requests.getFirst().getCreated().isAfter(requests.get(1).getCreated()));
    }
}