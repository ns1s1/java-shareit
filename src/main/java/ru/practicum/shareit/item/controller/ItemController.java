package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final static String USER_ID_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEAD) long id,
                          @RequestBody @Valid PostItemDto postItemDto) {
        return itemService.create(id, postItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_ID_HEAD) long userId,
                           @PathVariable("itemId") long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(USER_ID_HEAD) long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam("text") String searchString) {
        return itemService.searchItem(searchString);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") long itemId,
                          @RequestHeader(USER_ID_HEAD) long userId,
                          @RequestBody PatchItemDto patchItemDto) {

        return itemService.update(itemId, patchItemDto, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }
}