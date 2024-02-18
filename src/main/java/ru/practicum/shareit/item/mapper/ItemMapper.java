package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "requestId", target = "request.id")
    Item convertToItemDto(ItemCreateRequestDto itemCreateRequestDto);

    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto convertToResponseDto(Item item);


}