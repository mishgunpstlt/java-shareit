package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto bookingDto, Long userId);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approve);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookingByUser(Long userId, String state);

    List<BookingResponseDto> getAllBookingByOwner(Long userId, String state);

}
