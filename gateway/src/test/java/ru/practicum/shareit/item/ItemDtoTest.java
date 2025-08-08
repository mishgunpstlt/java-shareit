package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenNameIsBlank_validationFails() {
        ItemDto dto = new ItemDto(1L, 2L, "  ", "Description", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void whenDescriptionIsBlank_validationFails() {
        ItemDto dto = new ItemDto(1L, 2L, "Drill", " ", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void whenAvailableIsNull_validationFails() {
        ItemDto dto = new ItemDto(1L, 2L, "Drill", "Desc", null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("available"));
    }

    @Test
    void whenAllFieldsValid_noViolations() {
        ItemDto dto = new ItemDto(1L, 2L, "Drill", "Desc", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
