package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWitchRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;


    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = findUserForRequest(userId);
        ItemRequest itemRequest = itemRequestMapper.convertToItemRequest(itemRequestCreateDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setOwner(requestor);

        return itemRequestMapper.convertToItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        findUserForRequest(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<ItemWitchRequest> items = findItems(itemRequest.getId());

        ItemRequestResponseDto itemRequestResponse = new ItemRequestResponseDto();
        itemRequestResponse.setId(itemRequest.getId());
        itemRequestResponse.setCreated(itemRequest.getCreated());
        itemRequestResponse.setDescription(itemRequest.getDescription());
        itemRequestResponse.setItems(items);

        return itemRequestResponse;
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequestsByUserId(Long userId) {
        findUserForRequest(userId);

        return convertToListItemRequestResponseDto(itemRequestRepository.findAllByOwnerIdOrderByCreatedDesc(userId));
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        findUserForRequest(userId);

        Pageable page = PageRequest.of(from / size, size, Sort.by("created").descending());


        return convertToListItemRequestResponseDto(itemRequestRepository.findByOwnerIdNot(userId, page));
    }

    private User findUserForRequest(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c данным id не найден"));

    }

    private List<ItemWitchRequest> findItems(Long requestId) {
        return itemRepository.findByRequestIdOrderByIdDesc(requestId).stream()
                .map(this::convertToItemWitchRequest)
                .collect(Collectors.toList());
    }

    private ItemWitchRequest convertToItemWitchRequest(Item item) {
        return ItemWitchRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    private List<ItemRequestResponseDto> convertToListItemRequestResponseDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(itemRequest -> {
            ItemRequestResponseDto itemRequestResponse = new ItemRequestResponseDto();
            itemRequestResponse.setId(itemRequest.getId());
            itemRequestResponse.setCreated(itemRequest.getCreated());
            itemRequestResponse.setDescription(itemRequest.getDescription());
            itemRequestResponse.setItems(findItems(itemRequest.getId()));
            return itemRequestResponse;
        }).collect(Collectors.toList());
    }
}
