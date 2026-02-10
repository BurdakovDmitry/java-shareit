package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);
}
