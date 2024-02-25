package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private CommentResponseDto commentResponse;

    private Long requestId;
}