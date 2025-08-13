package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToReq;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional()
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        userService.getUserById(userId);
        List<ItemDto> items = itemService.searchItemsByText(itemRequestDto.getDescription());
        if (items.isEmpty()) {
            itemRequestDto.setRequesterId(userId);
            ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toEntity(itemRequestDto));
            return ItemRequestMapper.toDto(itemRequest);
        } else {
            throw new NotMetConditions("Вещь с таким описанием существует: " + itemRequestDto.getDescription());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponse> getAllRequestsByUser(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);

        return fillRequests(itemRequests);
    }

    @Override
    public List<ItemRequestResponse> getAllRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId);

        return fillRequests(itemRequests);
    }

    @Override
    public ItemRequestResponse getRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(()
                -> new NotFoundException("Запрос с id=" + requestId + " не найден"));
        return fillRequests(List.of(itemRequest)).getFirst();
    }


    private List<ItemRequestResponse> fillRequests(List<ItemRequest> itemRequests) {
        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<ItemToReq> itemToReqs = itemRepository.findAllByRequestIdIn(itemRequestIds).stream()
                .map(ItemMapper::toItemToReq)
                .toList();

        Map<Long, List<ItemToReq>> itemsByRequestId = itemToReqs.stream()
                .collect(Collectors.groupingBy(ItemToReq::getRequestId));

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestResponse(
                        itemRequest, itemsByRequestId.getOrDefault(itemRequest.getId(), List.of())))
                .sorted(Comparator.comparing(ItemRequestResponse::getCreated).reversed())
                .toList();
    }
}
