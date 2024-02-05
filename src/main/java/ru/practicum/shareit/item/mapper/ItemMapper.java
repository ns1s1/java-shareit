package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item convertToItemDto(ItemCreateRequestDto itemCreateRequestDto);

    Item convertToItemDto(ItemUpdateRequestDto itemUpdateRequestDto);

    ItemResponseDto convertToResponseDto(Item item);
}