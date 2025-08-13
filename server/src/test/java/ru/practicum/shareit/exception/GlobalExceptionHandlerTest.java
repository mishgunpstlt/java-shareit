package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleExistingEmailException_returnsErrorResponseWithConflict() {
        ExistingEmailException ex = new ExistingEmailException("Email already exists");

        ErrorResponse response = handler.handleExistingEmailException(ex);

        assertThat(response.getMessage()).isEqualTo("Email already exists");
    }

    @Test
    void handleNotFoundException_returnsErrorResponseWithNotFound() {
        NotFoundException ex = new NotFoundException("Not found");

        ErrorResponse response = handler.handleNotFoundException(ex);

        assertThat(response.getMessage()).isEqualTo("Not found");
    }

    @Test
    void handleNoOwnerException_returnsErrorResponseWithNotFound() {
        IsntOwnerException ex = new IsntOwnerException("Not owner");

        ErrorResponse response = handler.handleNoOwnerException(ex);

        assertThat(response.getMessage()).isEqualTo("Not owner");
    }

    @Test
    void handleNotAvailableException_returnsErrorResponseWithBadRequest() {
        NotAvailableException ex = new NotAvailableException("Not available");

        ErrorResponse response = handler.handleNotAvailableException(ex);

        assertThat(response.getMessage()).isEqualTo("Not available");
    }

    @Test
    void handleNotMetConditionsException_returnsErrorResponseWithBadRequest() {
        NotMetConditions ex = new NotMetConditions("Conditions not met");

        ErrorResponse response = handler.handleNotMetConditionsException(ex);

        assertThat(response.getMessage()).isEqualTo("Conditions not met");
    }
}
