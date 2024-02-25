package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemWitchBookingResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItemResponseDto lastBooking;

    private BookingForItemResponseDto nextBooking;

    private List<CommentResponseDto> comments;
}
