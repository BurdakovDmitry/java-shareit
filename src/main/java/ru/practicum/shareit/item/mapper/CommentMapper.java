package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "item", ignore = true)
    Comment mapToComment(CommentDto commentDto);

    CommentDto mapToCommentDto(Comment comment);
}
