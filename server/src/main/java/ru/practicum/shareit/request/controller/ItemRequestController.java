package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader(USER_ID_HEAD) Long userId,
                                         @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(@RequestHeader(USER_ID_HEAD) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_ID_HEAD) Long userId,
                                                       @RequestParam Integer from,
                                                       @RequestParam Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllRequestsByUserId(@RequestHeader(USER_ID_HEAD) Long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }
}
