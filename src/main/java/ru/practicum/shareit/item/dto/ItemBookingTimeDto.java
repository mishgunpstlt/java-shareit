package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemBookingTimeDto {

    private Long id;

    private Long ownerId;

    private String name;

    private String description;

    private Boolean available;

    private Instant nextBooking;

    private Instant lastBooking;

    private List<CommentDto> comments;
}
