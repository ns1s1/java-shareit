package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "itemId", target = "item.id")
    Booking convertToBooking(BookingCreateRequestDto bookingCreateRequestDto);

    BookingResponseDto convertToBookingResponseDto(Booking booking);

    List<BookingResponseDto> convertToListBookingResponseDto(List<Booking> bookings);
}
