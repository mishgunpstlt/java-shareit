package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.IsntOwnerException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userService.existUser(userId);
        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(()
                    -> new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не найден"));;
        }
        Item item = ItemMapper.toEntity(itemDto);
        item.setOwnerId(userId);
        itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdatingItemDto itemDto, Long itemId, Long userId) {
        userService.existUser(userId);
        Item updatingItem = getItem(itemId);
        if (Objects.equals(updatingItem.getOwnerId(), userId)) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                updatingItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                updatingItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updatingItem.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(updatingItem);
            return ItemMapper.toDto(updatingItem);
        } else {
            throw new IsntOwnerException("Только собственник вещи может ее обновить");
        }
    }

    @Override
    public ItemBookingTimeDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь c id=" + itemId + " не существует"));

        ItemBookingTimeDto itemBookingTimeDto = ItemMapper.toItemBookingDto(ItemMapper.toDto(item));
        fillComments(List.of(itemBookingTimeDto));

        return itemBookingTimeDto;
    }

    @Override
    @Transactional
    public List<ItemBookingTimeDto> getAllItemsByUserId(Long userId) {
        userService.existUser(userId);
        List<ItemBookingTimeDto> items = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .map(ItemMapper::toItemBookingDto)
                .toList();
        fillLastNextBooking(items);
        fillComments(items);
        return items;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public void isAvailable(ItemBookingTimeDto item) {
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь с id=" + item.getId() + " недоступна для бронирования");
        }
    }

    @Transactional
    @Override
    public CommentAuthorNameDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        UserDto user = userService.getUserById(userId);
        getItem(itemId);
        isBooker(userId, itemId);
        Comment comment = CommentMapper.toEntity(commentDto, userId, itemId);
        commentRepository.save(comment);
        return CommentMapper.toCommentAuthorNameDto(comment, user.getName());
    }

    private void isBooker(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.getAllBookingByBooker(userId);
        boolean hasPastBooking = bookings.stream().anyMatch(b ->
                b.getItem().getId().equals(itemId) &&
                        b.getEnd().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC)) &&
                        b.getBookingStatus() == BookingStatus.APPROVED
        );
        if (!hasPastBooking) {
            throw new NotMetConditions("Комментарий может оставить только пользователь, " +
                    "бронировавший вещь и завершивший аренду.");
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь c id=" + itemId + " не существует"));
    }

    private void fillComments(List<ItemBookingTimeDto> items) {
        List<Long> itemIds = items.stream().map(ItemBookingTimeDto::getId).toList();
        List<CommentDto> commentsDto = commentRepository.findAllByItemIdIn(itemIds).stream()
                .map(CommentMapper::toDto)
                .toList();
        Map<Long, List<CommentDto>> commentsByItemId = commentsDto.stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        for (ItemBookingTimeDto item : items) {
            item.setComments(commentsByItemId.getOrDefault(item.getId(), Collections.emptyList()));
        }
    }

    private void fillLastNextBooking(List<ItemBookingTimeDto> items) {
        List<Long> itemIds = items.stream().map(ItemBookingTimeDto::getId).toList();
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);

        Instant current = Instant.now();

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        for (ItemBookingTimeDto item : items) {
            List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());

            List<Booking> sortedBookings = itemBookings.stream()
                    .sorted(Comparator.comparing(Booking::getStart))
                    .toList();

            Instant lastBooking = null;
            Instant nextBooking = null;

            for (Booking booking : sortedBookings) {
                if (booking.getStart().isBefore(current) || booking.getStart().equals(current)) {
                    if (lastBooking == null || booking.getStart().isAfter(lastBooking)) {
                        lastBooking = booking.getStart();
                    }
                } else if (nextBooking == null || booking.getStart().isBefore(nextBooking)) {
                    nextBooking = booking.getStart();
                }
            }

            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
    }
}
