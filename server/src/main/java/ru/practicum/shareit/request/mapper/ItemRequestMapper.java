package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest convertToItemRequest(ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestResponseDto convertToItemRequestResponseDto(ItemRequest itemRequest);

    List<ItemRequestResponseDto> convertToList(List<ItemRequest> itemRequests);

}
