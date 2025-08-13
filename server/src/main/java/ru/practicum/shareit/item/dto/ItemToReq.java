package ru.practicum.shareit.item.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemToReq {

    private Long id;

    private String name;

    private Long ownerId;

    @JsonIgnore
    private Long requestId;
}
