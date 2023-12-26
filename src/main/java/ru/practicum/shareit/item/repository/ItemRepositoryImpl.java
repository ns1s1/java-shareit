package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private long id = 0;
    private static final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(generatedId());
        items.put(item.getId(), item);
        return item;
    }


    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        } else {
            throw new NotFoundException("Вещь с указанным id не найдена");
        }
        return item;
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь с указанным id не найдена");
        }

    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        if (items.containsKey(id)) {
            items.remove(id);
        } else {
            throw new NotFoundException("Вещь с таким id не найдена");
        }
    }


    private Long generatedId() {
        return ++id;
    }
}
