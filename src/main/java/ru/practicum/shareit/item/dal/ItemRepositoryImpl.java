package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepositoryImpl {

    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    public Item addItem(Item item, Long userId) {
        item.setId(nextId);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        nextId++;
        return item;
    }

    public Item updateItem(Item item, Long itemId) {
        Item updatingItem = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatingItem);
        return updatingItem;
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> getAllItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList();
    }

    public List<Item> searchItemsByText(String text) {
        String finalText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getDescription().toLowerCase().contains(finalText)
                        || item.getName().toLowerCase().contains(finalText)))
                .toList();
    }
}
