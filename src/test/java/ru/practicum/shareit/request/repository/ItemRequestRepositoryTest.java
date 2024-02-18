package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private final Pageable pageable = PageRequest.of(0, 10);

    private User user1;

    private User user2;

    private ItemRequest itemRequest1;

    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("user1")
                .email("user1@mai.ru")
                .build();
        user2 = User.builder()
                .name("user2")
                .email("user2@mai.ru")
                .build();
        itemRequest1 = ItemRequest.builder()
                .description("description1")
                .owner(user1)
                .created(LocalDateTime.now())
                .build();
        itemRequest2 = ItemRequest.builder()
                .description("description2")
                .owner(user2)
                .created(LocalDateTime.now())
                .build();
    }


    @Test
    void testFindByOwnerIdNot() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> requests = itemRequestRepository.findByOwnerIdNot(user1.getId(), pageable);

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "description2");
    }

    @Test
    void testFindAllByOwnerIdOrderByCreatedDesc() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> requests = itemRequestRepository.findAllByOwnerIdOrderByCreatedDesc(user1.getId());

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "description1");
    }
}