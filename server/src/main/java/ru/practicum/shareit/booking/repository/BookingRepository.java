package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);


    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                                            LocalDateTime start,
                                                                                            LocalDateTime end,
                                                                                            Pageable pageable);

    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start,
                                                                       Pageable pageable);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdInAndStatusAndStartLessThanOrderByStart(List<Long> itemsId,
                                                                      BookingStatus status,
                                                                      LocalDateTime start);

    List<Booking> findByItemIdInAndStatusAndStartGreaterThanOrderByStartDesc(List<Long> itemsId,
                                                                             BookingStatus status,
                                                                             LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                                               LocalDateTime start,
                                                                                               LocalDateTime end,
                                                                                               Pageable pageable);

    Booking findFirstByItemIdAndStatusAndStartLessThanOrderByStartDesc(Long itemId, BookingStatus status,
                                                                       LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(Long itemId, BookingStatus status,
                                                                      LocalDateTime start);

    Booking findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(Long itemId, Long userId, LocalDateTime end,
                                                                BookingStatus status);
  }
