package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.BookingForItemResponse;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemForResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItemResponse lastBooking;

    private BookingForItemResponse nextBooking;

    private List<CommentResponseDto> comments;
}
