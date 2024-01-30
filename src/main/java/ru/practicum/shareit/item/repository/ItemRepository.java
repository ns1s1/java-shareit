package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    @Query(" SELECT i FROM Item i "
            + "WHERE (LOWER(i.name) LIKE lower(CONCAT('%', ?1, '%')) OR "
            + "         LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) "
            + "AND i.available = true ")
    List<Item> search(String text);
}
