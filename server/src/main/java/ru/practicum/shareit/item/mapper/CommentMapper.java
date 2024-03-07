package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment convertToComment(CommentCreateRequestDto commentCreateRequestDto);

    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto convertToCommentResponseDto(Comment comment);

    List<CommentResponseDto> convertToCommentResponseDto(List<Comment> comments);
}
