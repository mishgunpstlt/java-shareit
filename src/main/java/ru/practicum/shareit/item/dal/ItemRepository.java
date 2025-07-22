package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item addItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    List<Item> searchItemsByText(String text);
}
