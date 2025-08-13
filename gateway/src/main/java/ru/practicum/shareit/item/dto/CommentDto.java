package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank
    private String text;

    private Instant created;

    private Long authorId;

    private Long itemId;
}
