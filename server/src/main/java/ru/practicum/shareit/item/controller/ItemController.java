package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public ItemResponseDto create(@RequestHeader(USER_ID_HEAD) Long ownerId,
                                  @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
        return itemService.create(ownerId, itemCreateRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemWitchBookingResponseDto getById(@RequestHeader(USER_ID_HEAD) Long userId,
                                               @PathVariable("itemId") Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemWitchBookingResponseDto> getAllUserItems(@RequestHeader(USER_ID_HEAD) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> itemSearch(@RequestHeader(USER_ID_HEAD) Long userId,
            @RequestParam("text") String searchString) {
        return itemService.searchItem(userId, searchString);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable("itemId") Long itemId,
                                  @RequestHeader(USER_ID_HEAD) Long userId,
                                  @RequestBody ItemUpdateRequestDto itemUpdateRequestDto) {

        return itemService.update(itemId, itemUpdateRequestDto, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody CommentCreateRequestDto request) {
        return itemService.createComment(userId, itemId, request);
    }
}