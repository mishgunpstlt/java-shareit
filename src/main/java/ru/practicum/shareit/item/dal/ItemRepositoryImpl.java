package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if (item.getName() != null) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatingItem);
        return updatingItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList();
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }
}
