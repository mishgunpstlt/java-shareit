package ru.practicum.shareit.item.model;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class Item {

    @NotNull
    private Long id;

    @NotNull
    private Long ownerId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private ItemRequest request;
}
