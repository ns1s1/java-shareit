package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PatchItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
}
