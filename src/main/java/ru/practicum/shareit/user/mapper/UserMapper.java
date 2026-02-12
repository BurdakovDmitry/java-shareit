package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User mapToUser(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    void updateMapToUser(UserDto userDto, @MappingTarget User user);

    UserDto mapToUserDto(User user);
}
