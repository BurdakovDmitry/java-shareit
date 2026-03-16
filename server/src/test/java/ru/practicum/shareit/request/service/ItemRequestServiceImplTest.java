package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper requestMapper;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestDto requestDto;
    private ItemRequestGetDto requestGetDto;
    private final Long userId = 1L;
    private final Long requestId = 5L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail("test@test.com");
        user.setName("User");

        request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        requestDto = new ItemRequestDto(requestId, "Description", null, request.getCreated());
        requestGetDto =
                new ItemRequestGetDto(requestId, "Description", null, LocalDateTime.now(), List.of());
    }

    @Test
    void create() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestMapper.mapToItemRequest(requestDto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.mapToItemRequestDto(request)).thenReturn(requestDto);

        ItemRequestDto saved = requestService.create(user.getId(), requestDto);

        assertNotNull(saved);
        assertEquals("Description", saved.description());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void createNotFoundUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(99L, requestDto));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getItemRequestByUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorOrderByCreatedDesc(user)).thenReturn(List.of(request));
        when(requestMapper.mapToItemRequestGetDto(request)).thenReturn(requestGetDto);

        List<ItemRequestGetDto> requests = requestService.getItemRequestByUser(userId);

        assertEquals(1, requests.size());
        assertEquals(requestGetDto, requests.getFirst());
        verify(requestRepository, times(1)).findAllByRequestorOrderByCreatedDesc(user);
    }

    @Test
    void getAllItemRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorNotOrderByCreatedDesc(user)).thenReturn(List.of(request));
        when(requestMapper.mapToItemRequestGetDto(request)).thenReturn(requestGetDto);

        List<ItemRequestGetDto> requests = requestService.getAllItemRequest(userId);

        assertEquals(1, requests.size());
        assertEquals(requestGetDto, requests.getFirst());
        verify(requestRepository, times(1)).findAllByRequestorNotOrderByCreatedDesc(user);
    }

    @Test
    void getItemRequestById() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestMapper.mapToItemRequestGetDto(request)).thenReturn(requestGetDto);

        ItemRequestGetDto getRequest = requestService.getItemRequestById(userId, requestId);

        assertNotNull(getRequest);
        assertEquals(getRequest.getDescription(), requestGetDto.getDescription());
        verify(requestRepository, times(1)).findById(requestId);
    }
}