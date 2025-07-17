package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    private Long id;
    private String name;
    private User requestor;
    private final Instant created = Instant.now();
}
