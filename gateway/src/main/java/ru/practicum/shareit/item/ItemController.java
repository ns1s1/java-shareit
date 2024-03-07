package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEAD) Long ownerId,
                                         @RequestBody @Valid ItemCreateRequest itemCreateRequest) {
        return itemClient.create(ownerId, itemCreateRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEAD) Long userId,
                                          @PathVariable("itemId") Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(USER_ID_HEAD) Long userId) {
        return itemClient.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID_HEAD) Long userId,
                                             @RequestParam("text") String searchString) {
        return itemClient.searchItem(userId, searchString);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") Long itemId,
                                         @RequestHeader(USER_ID_HEAD) Long userId,
                                         @RequestBody @Valid ItemUpdateRequest itemUpdateRequest) {

        return itemClient.update(userId, itemId, itemUpdateRequest);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemClient.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentCreateRequest request) {
        return itemClient.createComment(userId, itemId, request);
    }
}
