package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;

    private User owner;
    private Item item;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemRequest itemRequest;

    @BeforeEach
    public void init() {
        owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("itemRequest description")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("itemRequest description")
                .build();


    }

    @Test
    void testCreateCorrectItem() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestMapper.convertToItemRequest(itemRequestCreateDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.convertToItemRequestResponseDto(itemRequest)).thenReturn(itemRequestResponseDto);

        ItemRequestResponseDto actual = itemRequestService.create(1L, itemRequestCreateDto);

        assertEquals(itemRequestResponseDto, actual);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void testGetAllRequestsCorrect() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findByOwnerIdNot(anyLong(), any(Pageable.class))).thenReturn(List.of(itemRequest));

        List<ItemRequestResponseDto> actual = itemRequestService.getAllRequests(1L, 0, 10);

        assertEquals(itemRequest.getId(), actual.get(0).getId());
        assertEquals(itemRequest.getDescription(), actual.get(0).getDescription());
        assertEquals(0, actual.get(0).getItems().size());
    }

    @Test
    void testGetItemRequestById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findById(9999L)).thenReturn(Optional.empty());
        ItemRequestResponseDto expectedItemRequest = itemRequestService.getItemRequestById(1L, 1L);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 9999L));

        assertEquals(itemRequest.getId(), expectedItemRequest.getId());
        assertEquals("Запрос не найден", notFoundException.getMessage());

    }
}