package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemResponseDto create(Long id, ItemCreateRequestDto item);

    ItemResponseDto update(Long id, ItemUpdateRequestDto itemUpdateRequestDto, Long userId);

    List<ItemWitchBookingResponseDto> getAllItemsByUserId(Long userId);

    ItemWitchBookingResponseDto getById(Long userId, Long itemId);

    List<ItemResponseDto> searchItem(Long userId, String text);

    void delete(Long id);

    CommentResponseDto createComment(Long userId, Long itemId, CommentCreateRequestDto commentCreateRequestDto);
}
