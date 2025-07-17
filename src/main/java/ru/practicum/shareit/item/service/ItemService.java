package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatingItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(UpdatingItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItemsByUserId(Long userId);

    List<ItemDto> searchItemsByText(String text);
}
