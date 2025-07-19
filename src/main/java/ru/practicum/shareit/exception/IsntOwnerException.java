package ru.practicum.shareit.exception;

public class IsntOwnerException extends RuntimeException {
    public IsntOwnerException(String message) {
        super(message);
    }
}
