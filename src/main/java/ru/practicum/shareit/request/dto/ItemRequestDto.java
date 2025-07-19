package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {
    private final Instant created = Instant.now();
    private Long id;
    private String name;
    private User requestor;
}
