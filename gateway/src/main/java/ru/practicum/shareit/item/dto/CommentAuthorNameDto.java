package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CommentAuthorNameDto {
    private Long id;

    private String text;

    private Instant created;

    private String authorName;

    private Long itemId;
}
