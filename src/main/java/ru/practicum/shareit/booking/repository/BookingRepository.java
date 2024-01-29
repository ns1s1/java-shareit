package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                                            LocalDateTime start,
                                                                                            LocalDateTime end);

    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByItemIdInAndStatusAndStartLessThanOrderByStart(List<Long> itemsId,
                                                                      BookingStatus status,
                                                                      LocalDateTime start);

    List<Booking> findByItemIdInAndStatusAndStartGreaterThanOrderByStartDesc(List<Long> itemsId,
                                                                             BookingStatus status,
                                                                             LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                                               LocalDateTime start,
                                                                                               LocalDateTime end);

    Booking findFirstByItemIdAndStatusAndStartLessThanOrderByStartDesc(Long itemId, BookingStatus status,
                                                                       LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(Long itemId, BookingStatus status,
                                                                      LocalDateTime start);

    Booking findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(Long itemId, Long userId, LocalDateTime end,
                                                                BookingStatus status);


}