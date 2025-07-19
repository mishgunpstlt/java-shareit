package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IsntOwnerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        existUser(userId);
        Item item = ItemMapper.toEntity(itemDto);
        item = itemRepository.addItem(item, userId);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(UpdatingItemDto itemDto, Long itemId, Long userId) {
        existUser(userId);
        ItemDto item = getItemById(itemId);
        if (Objects.equals(item.getOwnerId(), userId)) {
            Item newItem = itemRepository.updateItem(ItemMapper.toUpdatingItem(itemDto), itemId);
            return ItemMapper.toDto(newItem);
        } else {
            throw new IsntOwnerException("Только собственник вещи может ее обновить");
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Optional<Item> item = itemRepository.getItemById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь c id=" + itemId + " не существует");
        }
        return ItemMapper.toDto(item.get());
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        existUser(userId);
        return itemRepository.getAllItemsByUserId(userId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private void existUser(Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь c id=" + userId + " не существует");
        }
    }
}
