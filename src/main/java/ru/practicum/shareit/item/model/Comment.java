package ru.practicum.shareit.item.model;


import jakarta.persistence.*;
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

    private String review;

    @Column(name = "created")
    private Instant timestamp;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "item_id")
    private Long itemId;
}
