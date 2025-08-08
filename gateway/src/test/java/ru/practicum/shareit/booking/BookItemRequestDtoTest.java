package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BookItemRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenItemIdIsNull_thenValidationFails() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("itemId"));
    }

    @Test
    void whenStartIsNull_thenValidationFails() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, 10L,
                null,
                LocalDateTime.now().plusHours(2),
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
    }

    @Test
    void whenStartIsInPast_thenValidationFails() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, 10L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2),
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // @FutureOrPresent не пропускает прошлое время
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
    }

    @Test
    void whenEndIsNull_thenValidationFails() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, 10L,
                LocalDateTime.now().plusHours(1),
                null,
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void whenEndIsNotInFuture_thenValidationFails() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusHours(1),
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        // @Future не пропускает время в прошлом
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void whenAllFieldsValid_noViolations() {
        BookItemRequestDto dto = new BookItemRequestDto(1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenInvalidTime_thenValidationFails() {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, 10L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(2), null);

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingDto);

        assertThat(violations).anyMatch(v -> v.getMessage().equals("Время начала должно быть раньше времени окончания"));
    }
}
