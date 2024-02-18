package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(USER_ID_HEAD) Long userId,
                                     @RequestBody @Valid BookingCreateRequestDto bookingCreateRequestDto) {
        return bookingService.create(userId, bookingCreateRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader(USER_ID_HEAD) Long userId,
                                     @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(USER_ID_HEAD) Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBooker(@RequestHeader(USER_ID_HEAD) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingService.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestHeader(USER_ID_HEAD) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingService.getAllBookingsByOwnerId(userId, state, from, size);
    }
}
