package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.Instant;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getReview(), comment.getTimestamp(),
                comment.getAuthorId(), comment.getItemId());
    }

    public static Comment toEntity(CommentDto commentDto, Long userId, Long itemId) {
        return new Comment(null, commentDto.getText(), Instant.now(),
                userId, itemId);
    }

    public static CommentAuthorNameDto toCommentAuthorNameDto(Comment comment, String name) {
        return new CommentAuthorNameDto(comment.getId(), comment.getReview(), comment.getTimestamp(), name,
                comment.getItemId());
    }
}
