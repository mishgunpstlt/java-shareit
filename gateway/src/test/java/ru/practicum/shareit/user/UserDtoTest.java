package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userDto_validData_noViolations() {
        UserDto user = new UserDto(1L, "John Doe", "john.doe@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void userDto_blankName_violations() {
        UserDto user = new UserDto(1L, " ", "john.doe@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void userDto_invalidEmail_violations() {
        UserDto user = new UserDto(1L, "John", "invalid-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void updatingUserDto_validData_noViolations() {
        UpdatingUserDto update = new UpdatingUserDto("New Name", "new.email@example.com");
        Set<ConstraintViolation<UpdatingUserDto>> violations = validator.validate(update);

        assertThat(violations).isEmpty();
    }

    @Test
    void updatingUserDto_nullName_noViolation() {
        UpdatingUserDto update = new UpdatingUserDto(null, "new.email@example.com");
        Set<ConstraintViolation<UpdatingUserDto>> violations = validator.validate(update);

        assertThat(violations).isEmpty();
    }

    @Test
    void updatingUserDto_invalidEmail_violations() {
        UpdatingUserDto update = new UpdatingUserDto("New Name", "not-an-email");
        Set<ConstraintViolation<UpdatingUserDto>> violations = validator.validate(update);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
