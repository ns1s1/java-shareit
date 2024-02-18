package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;

    private Booking booking1;
    private Booking booking2;

    private final LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 1, 0);
    private final LocalDateTime endTime = LocalDateTime.of(2023, 2, 1, 1, 0);
    private final LocalDateTime startTime2 = LocalDateTime.now().plusDays(1);
    private final LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);

    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void init() {
        booker = userRepository.save(new User(1L, "booker", "booker@mail.ru"));
        owner = userRepository.save(new User(2L, "owner", "owner@mail.ru"));

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        booking1 = new Booking();
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStart(startTime);
        booking1.setEnd(endTime);
        booking1.setStatus(BookingStatus.APPROVED);

        booking2 = new Booking();
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStart(startTime2);
        booking2.setEnd(endTime2);
        booking2.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }


    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getBooker(), booker);
        assertEquals(bookings.get(1).getBooker(), booker);
    }

    @Test
    void testFindByItemOwnerIdOrderByStartDesc() {
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);

        assertEquals(actualBookings.size(), 2);
        assertEquals(actualBookings.get(0).getBooker(), booker);
        assertEquals(actualBookings.get(1).getBooker(), booker);
    }

    @Test
    void testFindByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        LocalDateTime searchTime = startTime.plusDays(1);

        List<Booking> bookings =
                bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                        booker.getId(), searchTime, searchTime, pageable);

        assertEquals(bookings.size(), 1);
    }

    @Test
    void testFindByBookerIdAndEndLessThanOrderByStartDesc() {
        LocalDateTime searchTime = endTime2.plusDays(2);

        List<Booking> bookings =
                bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(booker.getId(), searchTime, pageable);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getBooker(), booker);
        assertEquals(bookings.get(1).getBooker(), booker);
    }

    @Test
    void testFindByItemOwnerIdAndStartGreaterThanOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(
                owner.getId(), startTime, pageable);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByBookerIdAndStatusIsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(booker.getId(),
                BookingStatus.WAITING, pageable);

        assertEquals(bookings.size(), 1);
    }

    @Test
    void testFindByBookerIdAndStartGreaterThanOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(booker.getId(),
                startTime, pageable);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByItemOwnerIdAndEndLessThanOrderByStartDesc() {
        LocalDateTime searchTime = endTime2.plusDays(2);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(
                owner.getId(), searchTime, pageable);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByItemOwnerIdAndStatusIsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(
                owner.getId(), BookingStatus.APPROVED, pageable);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByItemIdInAndStatusAndStartLessThanOrderByStart() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusAndStartLessThanOrderByStart(
                List.of(item.getId()), BookingStatus.APPROVED, endTime2);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByItemIdInAndStatusAndStartGreaterThanOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusAndStartGreaterThanOrderByStartDesc(
                List.of(item.getId()), BookingStatus.WAITING, startTime);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        List<Booking> bookings =
                bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                        owner.getId(), startTime, endTime, pageable);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker(), booker);
    }

    @Test
    void testFindFirstByItemIdAndStatusAndStartLessThanOrderByStartDesc() {
        Booking booking = bookingRepository.findFirstByItemIdAndStatusAndStartLessThanOrderByStartDesc(
                item.getId(), BookingStatus.APPROVED, startTime2);

        assertEquals(booking, booking1);
    }

    @Test
    void testFindFirstByItemIdAndBookerIdAndEndLessThanAndStatus() {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(
                item.getId(), booker.getId(), endTime2, BookingStatus.APPROVED);

        assertEquals(booking, booking1);
    }
}

