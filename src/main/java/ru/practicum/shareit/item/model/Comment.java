package ru.practicum.shareit.item.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String review;

    @Column(name = "created")
    private Instant timestamp;

    @JoinColumn(name = "author_id")
    private Long authorId;

    @JoinColumn(name = "item_id")
    private Long itemId;
}
