package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private Instant created;

    private Long authorId;

    private Long itemId;
}
