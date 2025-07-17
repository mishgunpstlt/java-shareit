package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchItemsByText(String text);
}
