package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingForItemResponseMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForItemResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWitchBookingsMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForResponse;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingForItemResponseMapper bookingForItemResponseMapper;
    private final CommentMapper commentMapper;
    private final ItemWitchBookingsMapper itemWitchBookingsMapper;


    @Override
    @Transactional
    public ItemResponseDto create(Long id, ItemCreateRequestDto itemCreateRequestDto) {
        log.debug("[ItemServiceImpl][create] id = {}, itemCreateRequestDto = {}", id, itemCreateRequestDto);
        User owner = getUserById(id);

        Item item = itemMapper.convertToItemDto((itemCreateRequestDto));
        item.setOwner(owner);

        return itemMapper.convertToResponseDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto, Long userId) {
        log.debug("[ItemServiceImpl][update] itemId = {}, itemUpdateRequestDto = {}, userId = {}", itemId,
                itemUpdateRequestDto, userId);
        getUserById(userId);
        Item item = getOnlyItemById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Данный пользователь не может изменить вещь");
        } else {
            if (itemUpdateRequestDto.getName() != null) {
                item.setName(itemUpdateRequestDto.getName());
            }
            if (itemUpdateRequestDto.getDescription() != null) {
                item.setDescription(itemUpdateRequestDto.getDescription());
            }
            if (itemUpdateRequestDto.getAvailable() != null) {
                item.setAvailable(itemUpdateRequestDto.getAvailable());
            }
        }

        return itemMapper.convertToResponseDto(itemRepository.save(item));
    }

    @Override
    public List<ItemWitchBookingResponseDto> getAllItemsByUserId(Long userId) {
        log.debug("[ItemServiceImpl][getAllItemsByUserId] userId = {}", userId);
        getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = getListItems(userId);
        List<Long> itemIds = getListItemIds(items);

        Map<Long, Booking> lastBookings = findLastBooking(itemIds, BookingStatus.APPROVED, now);
        Map<Long, Booking> nextBookings = findNextBooking(itemIds, BookingStatus.APPROVED, now);
        Map<Long, List<Comment>> commentsMap = collectingComments(itemIds);

        List<ItemForResponse> itemWithBookings = new ArrayList<>();

        //Сбор итогового списка
        items.forEach(it -> {
            ItemForResponse item = ItemForResponse.builder()
                    .id(it.getId())
                    .name(it.getName())
                    .description(it.getDescription())
                    .available(it.getAvailable())
                    .lastBooking(bookingForItemResponseMapper.convert(lastBookings.get(it.getId())))
                    .nextBooking(bookingForItemResponseMapper.convert(nextBookings.get(it.getId())))
                    .comments(commentMapper.convertToCommentResponseDto(commentsMap.get(it.getId())))
                    .build();
            itemWithBookings.add(item);
        });

        return itemWitchBookingsMapper.convertToList(itemWithBookings);
    }

    @Override
    public ItemWitchBookingResponseDto getById(Long userId, Long itemId) {
        log.debug("[ItemServiceImpl][getById] userId = {}, itemId = {}", userId, itemId);
        LocalDateTime now = LocalDateTime.now();
        Item item = getOnlyItemById(itemId);
        BookingForItemResponse lastBookings = null;
        BookingForItemResponse nextBookings = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBookings = bookingForItemResponseMapper
                    .convert(bookingRepository.findFirstByItemIdAndStatusAndStartLessThanOrderByStartDesc(item.getId(),
                            BookingStatus.APPROVED, now));
            nextBookings = bookingForItemResponseMapper.convert(
                    bookingRepository.findFirstByItemIdAndStatusAndStartGreaterThanOrderByStart(item.getId(),
                            BookingStatus.APPROVED, now));
        }

        List<Comment> comments = commentRepository.findByItemId(item.getId());

        return itemWitchBookingsMapper.convert(ItemForResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBookings)
                .nextBooking(nextBookings)
                .comments(commentMapper.convertToCommentResponseDto(comments))
                .build());
    }

    @Override
    public List<ItemResponseDto> searchItem(String text) {
        log.debug("[ItemServiceImpl][getById] text = {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.search(text);
        return items.stream()
                .map(itemMapper::convertToResponseDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("[ItemServiceImpl][delete] id = {}", id);
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long itemId, CommentCreateRequestDto commentCreateRequestDto) {
        log.debug("[ItemServiceImpl][createComment] userId = {},itemId = {}, commentCreateRequestDto = {}", userId,
                itemId, commentCreateRequestDto);
        User author = getUserById(userId);
        Item item = getOnlyItemById(itemId);

        Booking userBooking = bookingRepository
                .findFirstByItemIdAndBookerIdAndEndLessThanAndStatus(itemId, userId, LocalDateTime.now(),
                        BookingStatus.APPROVED);

        if (userBooking == null) {
            throw new BookingException("Не было аренды данной вещи пользователем.");
        }

        Comment comment = commentMapper.convertToComment(commentCreateRequestDto);

        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setItem(item);

        return commentMapper.convertToCommentResponseDto(commentRepository.save(comment));
    }

    public Item getOnlyItemById(Long itemId) {
        log.debug("[ItemServiceImpl][getOnlyItemById] itemId = {}", itemId);
        if (itemId == null) {
            throw new RuntimeException("itemId = null");
        }

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с данным id не найден."));
    }

    private User getUserById(Long id) {
        log.debug("[ItemServiceImpl][getUserById] id = {}", id);
        if (id == null) {
            throw new RuntimeException("userId = null");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не найден."));
    }

    private Map<Long, Booking> findLastBooking(List<Long> itemIds, BookingStatus status, LocalDateTime now) {
        log.debug("[ItemServiceImpl][findLastBooking] itemIds = {}, status = {}, now = {} ", itemIds, status, now);
        Map<Long, Booking> lastBookings = new HashMap<>();
        bookingRepository.findByItemIdInAndStatusAndStartLessThanOrderByStart(itemIds, BookingStatus.APPROVED,
                        now)
                .forEach(it -> lastBookings.put(it.getItem().getId(), it));
        return lastBookings;
    }

    private Map<Long, Booking> findNextBooking(List<Long> itemIds, BookingStatus status, LocalDateTime now) {
        log.debug("[ItemServiceImpl][findNextBooking] itemIds = {}, status = {}, now = {} ", itemIds, status, now);
        Map<Long, Booking> nextBookings = new HashMap<>();
        bookingRepository.findByItemIdInAndStatusAndStartGreaterThanOrderByStartDesc(itemIds,
                        BookingStatus.APPROVED, now)
                .stream()
                .filter(it -> it.getStart().isAfter(LocalDateTime.now()))
                .forEach(it -> nextBookings.put(it.getItem().getId(), it));
        return nextBookings;
    }

    private List<Long> getListItemIds(List<Item> items) {
        log.debug("[ItemServiceImpl][getListItemIds] items = {}", items);
        return items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }

    private List<Item> getListItems(Long userId) {
        log.debug("[ItemServiceImpl][getListItems] userId = {}", userId);
        return itemRepository.findByOwnerIdOrderByIdAsc(userId);
    }

    private Map<Long, List<Comment>> collectingComments(List<Long> itemIds) {
        log.debug("[ItemServiceImpl][collectingComments] itemIds = {}", itemIds);
        return commentRepository.findByItemIdIn(itemIds).stream().collect(
                Collectors.groupingBy(comment -> comment.getItem().getId()));
    }
}
