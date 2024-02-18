package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingResponseDto bookingResponseDto;
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;


    @BeforeEach
    public void init() {
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(2L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .booker(userResponseDto)
                .item(itemResponseDto)
                .status(BookingStatus.WAITING)
                .build();

        booker = new User(1L, "booker", "booker@mail.ru");

        owner = new User(2L, "owner", "owner@mail.ru");

        item = Item.builder()
                .id(2L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();


    }

    @Test
    void testCreateBookingCorrect() {
        BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto();
        bookingCreateRequestDto.setItemId(2L);
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));

        when(bookingMapper.convertToBooking(bookingCreateRequestDto)).thenReturn(booking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.convertToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.create(1L, bookingCreateRequestDto);

        assertEquals(bookingResponseDto, actualBooking);
    }


    @Test
    void testCreateBookingNotAvailableItem() {
        BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto();
        bookingCreateRequestDto.setItemId(2L);
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));
        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingException bookingException = assertThrows(BookingException.class,
                () -> bookingService.create(2L, bookingCreateRequestDto));

        assertEquals("Вещь недоступна для бронирования", bookingException.getMessage());
    }

    @Test
    void testCreateBookingByOwnerItem() {
        BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto();
        bookingCreateRequestDto.setItemId(2L);
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.create(2L, bookingCreateRequestDto));

        assertEquals(notFoundException.getMessage(), "Это Ваша вещь");
    }

    @Test
    void testGetByIdUncorrectedOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotOwnerException notOwnerException = assertThrows(NotOwnerException.class,
                () -> bookingService.getById(3L, 1L));

        assertEquals(notOwnerException.getMessage(), "Нет доступа к бронированию.");
    }

    @Test
    void testGetByCorrect() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.getById(1L, 1L);

        assertEquals(actual, bookingResponseDto);
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void testBookingByNotOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        NotOwnerException notOwnerException = assertThrows(NotOwnerException.class,
                () -> bookingService.updateStatus(1L, 1L, true));

        assertEquals(notOwnerException.getMessage(), "Пользователь не владелец");
    }

    @Test
    void testApproveBookingByOwner() {
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(bookingMapper.convertToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actualBooking = bookingService.updateStatus(2L, 1L, true);

        assertEquals(actualBooking, bookingResponseDto);

    }

    @Test
    void approveApprovedBooking() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        BookingException bookingException = assertThrows(BookingException.class,
                () -> bookingService.updateStatus(2L, 1L, true));

        assertEquals(bookingException.getMessage(), "Бронирование уже подтверждено");
    }


    @Test
    void testGetAllBookingsByUserIdAndAllState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(any(), any());
    }

    @Test
    void testGetAllBookingsByUserIdAndPastState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "PAST", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndLessThanOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByUserIdAndFutureState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "FUTURE", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1)).findByBookerIdAndStartGreaterThanOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByUserIdAndCurrentState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                        anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "CURRENT", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(any(), any(), any(), any());
    }

    @Test
    void testGetAllBookingsByUserIdAndWaitingState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.WAITING.getClass()), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "WAITING", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusIsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByUserIdAndRejectedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.REJECTED.getClass()), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByUserId(1L, "REJECTED", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusIsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllByBookerIdUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByUserId(2L, "UNSUPPORTED", 0, 10));

        assertEquals(validationException.getMessage(), "Unknown state: UNSUPPORTED_STATUS");

    }

    @Test
    void testGetAllByUserIdNotFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10));

        assertEquals(notFoundException.getMessage(), "Пользователь с данным id не найден.");
    }

    @Test
    void testGetAllOwnersByUserIdAndAllState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "ALL", 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);
        verify(bookingRepository, times(1)).findByItemOwnerIdOrderByStartDesc(any(), any());
    }

    @Test
    void testGetAllBookingsByOwnerIdAndPastState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "PAST", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndLessThanOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByOwnerIdAndFutureState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "FUTURE", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByOwnerIdAndCurrentState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "CURRENT", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(any(), any(), any(), any());
    }

    @Test
    void testGetAllBookingsByOwnerIdAndWaitingState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.WAITING.getClass()), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "WAITING", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusIsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllBookingsByOwnerIdAndRejectedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.REJECTED.getClass()), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToListBookingResponseDto(List.of(booking))).thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwnerId(1L, "REJECTED", 0, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingResponseDto);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusIsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void testGetAllByOwnerIdUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(2L, "UNSUPPORTED", 0, 10));

        assertEquals(validationException.getMessage(), "Unknown state: UNSUPPORTED_STATUS");

    }

    @Test
    void testGetAllByOwnerIdNotFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByOwnerId(1L, "ALL", 0, 10));

        assertEquals(notFoundException.getMessage(), "Пользователь с данным id не найден.");
    }
}