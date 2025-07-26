package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toEntity(ItemDto itemDto) {
        return new Item(null, itemDto.getOwnerId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable());
    }

    public static Item toEntityFromFillItem(ItemBookingTimeDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getOwnerId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable());
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getOwnerId(), item.getName(), item.getDescription(),
                item.getAvailable());
    }

    public static Item toUpdatingItem(UpdatingItemDto updatingItemDto) {
        return new Item(null, null, updatingItemDto.getName(), updatingItemDto.getDescription(),
                updatingItemDto.getAvailable());
    }

    public static ItemBookingTimeDto toItemBookingDto(ItemDto itemDto) {
        return new ItemBookingTimeDto(itemDto.getId(), itemDto.getOwnerId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(), null, null, null);
    }
}
