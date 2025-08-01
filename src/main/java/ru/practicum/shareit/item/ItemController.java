package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление новой вещи в коллекцию items:  {}", itemDto);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody UpdatingItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление вещи в коллекции items:  {}", itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingTimeDto getItemById(@PathVariable Long itemId) {
        log.info("Получение вещи из коллекции items по id={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemBookingTimeDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение вещей из коллекции items по userId={}", userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("Поиск вещей из коллекции items по описанию: {}", text);
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentAuthorNameDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                           @RequestBody @Valid CommentDto commentDto) {
        log.info("Добавление комментария к вещи с id={}: {}", itemId, commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
