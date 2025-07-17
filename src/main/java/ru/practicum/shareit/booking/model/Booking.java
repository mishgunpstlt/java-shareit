package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private Long id;
    private Item item;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private User owner;
    private String review;
    private BookingStatus bookingStatus;
}
