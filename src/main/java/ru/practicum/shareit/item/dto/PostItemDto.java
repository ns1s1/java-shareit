package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PostItemDto {
    private Long id;
    @NotEmpty
    @NotBlank
    private String name;
    @NotEmpty
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
}