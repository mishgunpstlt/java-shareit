package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody @Valid BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового бронирования в коллекцию bookings:  {}", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId, @RequestParam @NotNull Boolean approved) {
        log.info("Подтверждение бронирования с id={} в коллекции bookings", bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Получение бронирования из коллекции bookings по id={}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирования из коллекции bookings по userId={}", userId);
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирования из коллекции bookings по ownerId={}", userId);
        return bookingService.getAllBookingByOwner(userId, state);
    }
}


