package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Item item);

    List<Item> getAllItemsByUserId(long userId);

    Item getById(long id);

    List<Item> searchItem(String text);

    void delete(long id);
}
