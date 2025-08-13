package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BookingMapper {

    public static Booking toNewEntity(BookingDto bookingDto, Long userId) {
        return new Booking(null, null, toInstant(bookingDto.getStart()),
                toInstant(bookingDto.getEnd()), null, BookingStatus.WAITING);
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getItem().getId(), toLocalDateTime(booking.getStart()),
                toLocalDateTime(booking.getEnd()), booking.getBooker(), booking.getBookingStatus());
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                toLocalDateTime(booking.getStart()),
                toLocalDateTime(booking.getEnd()),
                ItemMapper.toDto(booking.getItem()),
                UserMapper.toDto(booking.getBooker()),
                booking.getBookingStatus()
        );
    }
}
