package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ItemDto create(long id, PostItemDto postItemDto) {

        Item item = convertPostItemDtoToItem(postItemDto);
        item.setOwnerId(id);
        userRepository.getById(item.getOwnerId());

        return convertItemToItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(long id, PatchItemDto patchItemDto, long userId) {
        validatePatchItemDto(patchItemDto);
        userRepository.getById(userId);
        Item item = itemRepository.getById(id);
        if (item.getOwnerId() != userId) {
            throw new AccessDeniedException("Данный пользователь не может изменить вещь");
        }

        modelMapper.map(patchItemDto, item);

        return convertItemToItemDto(itemRepository.update(item));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        userRepository.getById(userId);

        return itemRepository.getAllItemsByUserId(userId).stream()
                .map(this::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long id) {
        return convertItemToItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> searchItem(String text) {

        return text.isBlank() ? new ArrayList<>() : itemRepository.searchItem(text).stream()
                .map(this::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        itemRepository.delete(id);
    }

    private Item convertPostItemDtoToItem(PostItemDto postItemDto) {
        return modelMapper.map(postItemDto, Item.class);
    }

    private ItemDto convertItemToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    private void validatePatchItemDto(PatchItemDto patchItemDto) {
        if (patchItemDto.getName() != null && (patchItemDto.getName().isEmpty() || patchItemDto.getName().isBlank())) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (patchItemDto.getDescription() != null && (patchItemDto.getDescription().isEmpty() || patchItemDto.getDescription().isBlank())) {
            throw new ValidationException("Описание не может быть пустым");
        }
    }
}
