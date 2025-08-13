package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDtoTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenTextIsBlank_validationFails() {
        CommentDto dto = new CommentDto(1L, "  ", Instant.now(), 1L, 1L);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("text"));
    }

    @Test
    void whenTextIsValid_noViolations() {
        CommentDto dto = new CommentDto(1L, "Nice item", Instant.now(), 1L, 1L);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
