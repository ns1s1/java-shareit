package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestResponseDto> getAllRequestsByUserId(Long userId);

    List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size);
}
