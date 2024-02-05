package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;


    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingCreateRequestDto bookingCreateRequestDto) {
        log.debug("[BookingServiceImpl][create] userId = {}, booking = {} ", userId, bookingCreateRequestDto);
        Long itemId = bookingCreateRequestDto.getItemId();
        User user = getUserById(userId);
        Item item = getItemById(itemId);

        if (!item.getAvailable()) {
            throw new BookingException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Это Ваша вещь");
        }
        if (!bookingCreateRequestDto.getEnd().isAfter(bookingCreateRequestDto.getStart())) {
            throw new BookingException("Дата завершения бронирование должна быть позже даты начала.");
        }

        Booking booking = bookingMapper.convertToBooking(bookingCreateRequestDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.convertToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        log.debug("[BookingServiceImpl][updateStatus] userId = {}, bookingId = {} , approved = {}", userId, bookingId,
                approved);
        Booking updatedBooking = getBookingById(bookingId);
        getUserById(userId);

        if (!updatedBooking.getItem().getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Пользователь не владелец");
        }
        if (updatedBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException("Бронирование уже подтверждено");
        }

        if (approved) {
            updatedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            updatedBooking.setStatus(BookingStatus.REJECTED);
        }

        return bookingMapper.convertToBookingResponseDto(bookingRepository.save(updatedBooking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        log.debug("[BookingServiceImpl][getById] userId = {}, bookingId = {}", userId, bookingId);
        Booking booking = getBookingById(bookingId);

        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new NotOwnerException("Нет доступа к бронированию.");
        }

        return bookingMapper.convertToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUserId(Long userId, String state) {
        log.debug("[BookingServiceImpl][getAllBookingsByUserId] userId = {}, state = {}", userId, state);
        getUserById(userId);

        switch (checkState(state)) {
            case ALL:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case PAST:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(
                                userId, LocalDateTime.now()));
            case FUTURE:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(
                                userId, LocalDateTime.now()));
            case CURRENT:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                                userId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING));
            case REJECTED:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedOperationException("Такого статуса нет");
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwnerId(Long ownerId, String state) {
        log.debug("[BookingServiceImpl][getAllBookingsByUserId] ownerId = {}, state = {}", ownerId, state);
        getUserById(ownerId);

        switch (checkState(state.toUpperCase())) {
            case ALL:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId));
            case PAST:
                return bookingMapper.convertToListBookingResponseDto(bookingRepository
                        .findByItemOwnerIdAndEndLessThanOrderByStartDesc(ownerId, LocalDateTime.now()));
            case FUTURE:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(ownerId,
                                LocalDateTime.now()));
            case CURRENT:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                                ownerId, LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(
                                ownerId, BookingStatus.WAITING));
            case REJECTED:
                return bookingMapper.convertToListBookingResponseDto(
                        bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(
                                ownerId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedOperationException("Такого статуса нет");
        }
    }

    private User getUserById(Long id) {
        log.debug("[BookingServiceImpl][getUserById] id = {}", id);
        if (id == null) {
            throw new RuntimeException("userId = null");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не найден."));
    }

    private Booking getBookingById(Long bookingId) {
        log.debug("[BookingServiceImpl][getBookingById] bookingId = {}", bookingId);
        if (bookingId == null) {
            throw new RuntimeException("bookingId = null");
        }

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private Item getItemById(Long itemId) {
        log.debug("[BookingServiceImpl][getItemById] itemId = {}", itemId);
        if (itemId == null) {
            throw new RuntimeException("itemId = null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
    }

    private BookingState checkState(String stringState) {
        log.debug("[BookingServiceImpl][checkState] stringState = {}", stringState);
        try {
            return BookingState.valueOf(stringState);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
