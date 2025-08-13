package ru.practicum.shareit.exception;

public class NotMetConditions extends RuntimeException {
    public NotMetConditions(String message) {
        super(message);
    }
}
