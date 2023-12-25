package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item create(Item item) {
        userRepository.getById(item.getOwnerId());

        return itemRepository.create(item);
    }

    @Override
    public Item update(Item item) {
        userRepository.getById(item.getOwnerId());

        return itemRepository.update(item);
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        userRepository.getById(userId);

        return itemRepository.getAllItemsByUserId(userId);
    }

    @Override
    public Item getById(long id) {
        return itemRepository.getById(id);
    }

    @Override
    public List<Item> searchItem(String text) {

        return text.isBlank() ? new ArrayList<>() : itemRepository.searchItem(text);
    }

    @Override
    public void delete(long id) {
        itemRepository.delete(id);
    }
}
