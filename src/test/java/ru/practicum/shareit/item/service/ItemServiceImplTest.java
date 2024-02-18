package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingForItemResponseMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWitchBookingsMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemWitchBookingsMapper itemWitchBookingsMapper;

    @Mock
    private BookingForItemResponseMapper bookingForItemResponseMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private ItemResponseDto itemResponseDto;
    private ItemCreateRequestDto itemCreateRequestDto;
    private Item item;
    private ItemRequest itemRequest;

    private ItemWitchBookingResponseDto itemWitchBookingResponseDto;
    private Booking booking;
    private CommentCreateRequestDto commentCreateRequestDto;
    private Comment comment;


    @BeforeEach
    public void init() {
        owner = User.builder()
                .id(2L)
                .name("user")
                .email("user@mail.ru")
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();


        itemCreateRequestDto = ItemCreateRequestDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        item = Item.builder()
                .id(2L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();

        itemWitchBookingResponseDto = ItemWitchBookingResponseDto.builder()
                .id(2L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .build();

        booking = Booking.builder()
                .status(BookingStatus.WAITING)
                .item(item)
                .build();

        commentCreateRequestDto = new CommentCreateRequestDto();
        commentCreateRequestDto.setText("test");

        comment = Comment.builder()
                .text("test")
                .build();
    }

    @Test
    void testCreateItemCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.convertToItemDto(itemCreateRequestDto)).thenReturn(item);
        when(itemMapper.convertToResponseDto(item)).thenReturn(itemResponseDto);

        ItemResponseDto expectedItem = itemService.create(1L, itemCreateRequestDto);

        assertEquals(expectedItem, itemResponseDto);
    }

    @Test
    void testCreateItemWithRequestIdCorrect() {
        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.convertToResponseDto(item)).thenReturn(itemResponseDto);
        when(itemMapper.convertToItemDto(itemCreateRequestDto)).thenReturn(item);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));


        ItemResponseDto expectedItem = itemService.create(1L, itemCreateRequestDto);

        assertEquals(expectedItem, itemResponseDto);
    }

    @Test
    void updateItemCorrect() {
        ItemUpdateRequestDto itemUpdateRequestDto = new ItemUpdateRequestDto();
        itemUpdateRequestDto.setName("update");
        itemUpdateRequestDto.setDescription("update description");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.convertToResponseDto(item)).thenReturn(itemResponseDto);

        ItemResponseDto actual = itemService.update(2L, itemUpdateRequestDto, 2L);

        assertEquals(actual, itemResponseDto);
    }

    @Test
    void getItemByIdCorrect() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemService.getById(owner.getId(), item.getId());

        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getAllByUserIdCorrect() {
        List<Item> items = List.of(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdOrderByIdAsc(2L)).thenReturn(items);
        when(itemWitchBookingsMapper.convertToList(any())).thenReturn(List.of(itemWitchBookingResponseDto));

        List<ItemWitchBookingResponseDto> actual = itemService.getAllItemsByUserId(2L);

        assertEquals(1, actual.size());
    }

    @Test
    void getCorrectItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemWitchBookingsMapper.convert(any())).thenReturn(itemWitchBookingResponseDto);

        ItemWitchBookingResponseDto actual = itemService.getById(2L, 2L);

        assertThat(actual.getId(), equalTo(item.getId()));
        assertThat(actual.getName(), equalTo(item.getName()));
        assertThat(actual.getDescription(), equalTo(item.getDescription()));
        assertThat(actual.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void searchItemsAvailableCorrect() {
        List<Item> items = List.of(item);
        when(itemRepository.search(any())).thenReturn(items);
        List<ItemResponseDto> expectedItems = itemService.searchItem("test");
        assertEquals(1, expectedItems.size());
    }

    @Test
    void testSearchItemsAvailableUncorrectedQuery() {
        List<Item> items = List.of(item);
        List<ItemResponseDto> expectedItems = itemService.searchItem("");
        assertEquals(0, expectedItems.size());
    }

    @Test
    void testCreateCommentCorrect() {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setText("test");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(any(), any(), any(),
                any())).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.convertToComment(commentCreateRequestDto)).thenReturn(comment);
        when(commentMapper.convertToCommentResponseDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto createdComment = itemService.createComment(1L, 1L,
                commentCreateRequestDto);

        assertEquals(comment.getText(), createdComment.getText());
    }

    @Test
    void testCreateCommentUncorrectedBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(any(), any(), any(),
                any())).thenReturn(null);
        BookingException bookingException = assertThrows(BookingException.class,
                () -> itemService.createComment(1L, 1L,
                        commentCreateRequestDto));

        assertEquals("Не было аренды данной вещи пользователем.", bookingException.getMessage());
    }


}