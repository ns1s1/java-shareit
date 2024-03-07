package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingForItemResponse {

    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
