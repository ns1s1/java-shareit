package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") long id,
                          @RequestBody @Valid PostItemDto postItemDto) {
        Item item = convertPostItemDtoToItem(postItemDto);
        item.setOwnerId(id);

        return convertItemToItemDto(itemService.create(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                           @PathVariable("itemId") long itemId) {
        return convertItemToItemDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsByUserId(userId).stream()
                .map(this::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam("text") String searchString) {
        return itemService.searchItem(searchString).stream()
                .map(this::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @RequestBody PatchItemDto patchItemDto) {
        validatePatchItemDto(patchItemDto);
        Item item = itemService.getById(itemId);

        if (item.getOwnerId() != userId) {
            throw new AccessDeniedException("Данный пользователь не может изменить вещь");
        }
        modelMapper.map(patchItemDto, item);

        return convertItemToItemDto(itemService.update(item));
    }

    private Item convertPostItemDtoToItem(PostItemDto postItemDto) {
        return modelMapper.map(postItemDto, Item.class);
    }

    private ItemDto convertItemToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    private void validatePatchItemDto(PatchItemDto patchItemDto) {
        if (patchItemDto.getName() != null && (patchItemDto.getName().isEmpty() || patchItemDto.getName().isBlank())) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (patchItemDto.getDescription() != null && (patchItemDto.getDescription().isEmpty() || patchItemDto.getDescription().isBlank())) {
            throw new ValidationException("Описание не может быть пустым");
        }
    }
}