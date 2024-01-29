package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingCreateRequestDto bookingCreateRequestDto);

    BookingResponseDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookingsByUserId(Long userId, String state);

    List<BookingResponseDto> getAllBookingsByOwnerId(Long userId, String state);
}
