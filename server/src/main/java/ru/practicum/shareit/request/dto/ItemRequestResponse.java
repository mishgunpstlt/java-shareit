package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemToReq;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestResponse {

    private Long id;

    private String description;

    private Instant created;

    private List<ItemToReq> items;
}
