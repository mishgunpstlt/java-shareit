package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long userId);

    List<ItemRequest> findAllByRequesterIdNot(Long userId);

    Optional<ItemRequest> findById(Long requestId);
}
