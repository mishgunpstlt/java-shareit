package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(UpdatingItemDto itemDto, Long itemId, Long userId);

    ItemBookingTimeDto getItemById(Long itemId);

    List<ItemBookingTimeDto> getAllItemsByUserId(Long userId);

    List<ItemDto> searchItemsByText(String text);

    void isAvailable(ItemBookingTimeDto item);

    CommentAuthorNameDto addComment(Long userId, Long itemId, CommentDto commentDto);

}
