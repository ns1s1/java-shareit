package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemWitchRequest {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}

