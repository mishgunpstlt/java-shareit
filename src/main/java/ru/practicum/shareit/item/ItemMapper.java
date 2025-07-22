package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatingItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toEntity(ItemDto itemDto) {
        return new Item(null, null, itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), null);
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getOwnerId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequest());
    }

    public static Item toUpdatingItem(UpdatingItemDto updatingItemDto) {
        return new Item(null, null, updatingItemDto.getName(), updatingItemDto.getDescription(),
                updatingItemDto.getAvailable(), null);
    }
}
