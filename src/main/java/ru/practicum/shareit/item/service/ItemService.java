package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.dto.PostItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long id, PostItemDto postItemDto);

    ItemDto update(long id, PatchItemDto patchItemDto, long userId);

    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto getById(long id);

    List<ItemDto> searchItem(String text);

    void delete(long id);
}
