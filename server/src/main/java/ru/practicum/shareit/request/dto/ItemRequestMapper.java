package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemToReq;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreated(), itemRequest.getRequester().getId());
    }

    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return new ItemRequest(null, itemRequestDto.getDescription(), Instant.now(), new User(itemRequestDto.getRequesterId(), null, null));
    }

    public static ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest, List<ItemToReq> itemToReqList) {
        return new ItemRequestResponse(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreated(), new ArrayList<>(itemToReqList));
    }
}
