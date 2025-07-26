package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    List<Item> findByNameLikeIgnoreCaseAndAvailableTrueOrDescriptionLikeIgnoreCaseAndAvailableTrue(String text, String stext);
}
