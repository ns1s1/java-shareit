package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemWitchBookingResponseDto;
import ru.practicum.shareit.item.model.ItemForResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemWitchBookingsMapper {
    ItemWitchBookingResponseDto convert(ItemForResponse itemForResponse);

    List<ItemWitchBookingResponseDto> convertToList(List<ItemForResponse> items);
}
