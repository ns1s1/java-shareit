package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForItemResponse;

@Mapper(componentModel = "spring")
public interface BookingForItemResponseMapper {

    @Mapping(source = "booker.id", target = "bookerId")
    BookingForItemResponse convert(Booking booking);
}
