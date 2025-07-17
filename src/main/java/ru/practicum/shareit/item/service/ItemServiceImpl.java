package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.List;
import java.util.Objects;

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
        }
        return null;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не существует");
        }
        return ItemMapper.toDto(item);
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
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private void existUser(Long userId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не существует");
        }
    }
}
