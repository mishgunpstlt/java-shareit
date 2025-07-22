package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Item addItem(Item item, Long userId) {
        item.setId(nextId);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        nextId++;
        return item;
    }

    @Override
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

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList();
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        String finalText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getDescription().toLowerCase().contains(finalText)
                        || item.getName().toLowerCase().contains(finalText)))
                .toList();
    }
}
