package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового запроса в коллекцию requests:  {}", itemRequestDto);
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestResponse> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов из коллекции requests по userId={}", userId);
        return itemRequestService.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запросов из коллекции requests c userId={}", userId);
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequestById(@PathVariable Long requestId) {
        log.info("Получение запроса из коллекции requests по requestId={}", requestId);
        return itemRequestService.getRequestById(requestId);
    }
}
