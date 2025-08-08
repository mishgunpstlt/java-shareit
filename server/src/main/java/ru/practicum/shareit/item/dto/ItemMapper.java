package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toEntity(ItemDto itemDto) {
        return new Item(null, itemDto.getOwnerId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), itemDto.getRequestId());
    }

    public static Item toEntityFromFillItem(ItemBookingTimeDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getOwnerId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), null);
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getOwnerId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId());
    }

    public static ItemBookingTimeDto toItemBookingDto(ItemDto itemDto) {
        return new ItemBookingTimeDto(itemDto.getId(), itemDto.getOwnerId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(), null, null, null);
    }

    public static ItemToReq toItemToReq(Item item) {
        return new ItemToReq(item.getId(), item.getName(), item.getOwnerId(), item.getRequestId());
    }
}
