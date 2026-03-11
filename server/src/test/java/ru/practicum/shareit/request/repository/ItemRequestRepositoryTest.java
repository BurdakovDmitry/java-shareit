package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

        userRepository.save(user);
        userRepository.save(newUser);
    }


    @Test
    void findAllByRequestorOrderByCreatedDesc() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request");
        request.setRequestor(user);
        requestRepository.save(request);

        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("newRequest");
        newRequest.setRequestor(newUser);
        requestRepository.save(newRequest);

        List<ItemRequest> requests = requestRepository.findAllByRequestorOrderByCreatedDesc(user);

        assertEquals(1, requests.size());
        assertEquals("Request", requests.getFirst().getDescription());
        assertEquals(user.getId(), requests.getFirst().getRequestor().getId());
    }

    @Test
    void findAllByRequestorNotOrderByCreatedDesc() throws InterruptedException {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("oldRequests");
        newRequest.setRequestor(newUser);
        requestRepository.save(newRequest);

        Thread.sleep(100);

        ItemRequest oldRequests = new ItemRequest();
        oldRequests.setDescription("newRequest");
        oldRequests.setRequestor(newUser);
        requestRepository.save(oldRequests);

        List<ItemRequest> requests = requestRepository.findAllByRequestorNotOrderByCreatedDesc(user);

        assertEquals(2, requests.size());
        assertEquals("newRequest", requests.getFirst().getDescription());
        assertTrue(requests.getFirst().getCreated().isAfter(requests.get(1).getCreated()));
    }
}