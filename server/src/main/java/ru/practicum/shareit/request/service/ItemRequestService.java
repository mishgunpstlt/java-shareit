package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponse> getAllRequestsByUser(Long userId);

    List<ItemRequestResponse> getAllRequests(Long userId);

    ItemRequestResponse getRequestById(Long requestId);
}
