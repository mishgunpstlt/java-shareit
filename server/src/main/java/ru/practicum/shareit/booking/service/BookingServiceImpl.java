package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dto.ItemBookingTimeDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {
        UserDto user = userService.getUserById(userId);
        ItemBookingTimeDto itemBookingTimeDto = itemService.getItemById(bookingDto.getItemId());
        itemService.isAvailable(itemBookingTimeDto);
        correctBookingTime(bookingDto);
        Booking booking = BookingMapper.toNewEntity(bookingDto, userId);
        booking.setBooker(UserMapper.toEntity(user));
        booking.setItem(ItemMapper.toEntityFromFillItem(itemBookingTimeDto));
        bookingRepository.save(booking);
        return BookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approve) {
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotMetConditions("Только владелец вещи может подтвердить бронирование");
        } else if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new NotMetConditions("Статус бронирования не в статусе ожидания");
        }
        userService.existUser(userId);
        if (approve) {
            booking.setBookingStatus(APPROVED);
            bookingRepository.approveBooking(bookingId, BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
            bookingRepository.approveBooking(bookingId, BookingStatus.REJECTED);
        }
        return BookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        userService.existUser(userId);
        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotMetConditions("Просмотреть бронирование может либо автор бронирования, либо владелец вещи");
        }
        return BookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingByUser(Long userId, String state) {
        userService.existUser(userId);
        return switch (state) {
            case "ALL" -> bookingRepository.getAllBookingByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "CURRENT" -> bookingRepository.getCurrentBookingsByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "PAST" -> bookingRepository.getPastBookingsByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "FUTURE" -> bookingRepository.getFutureBookingsByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "WAITING" -> bookingRepository.getWaitingBookingsByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "REJECTED" -> bookingRepository.getRejectedBookingsByBooker(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            default -> throw new NotMetConditions("Неправильный параметр запроса (state)");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingByOwner(Long userId, String state) {
        userService.existUser(userId);
        return switch (state) {
            case "ALL" -> bookingRepository.getAllBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "CURRENT" -> bookingRepository.getCurrentBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "PAST" -> bookingRepository.getPastBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "FUTURE" -> bookingRepository.getFutureBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "WAITING" -> bookingRepository.getWaitingBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            case "REJECTED" -> bookingRepository.getRejectedBookingsByOwner(userId).stream()
                    .map(BookingMapper::toResponseDto)
                    .toList();
            default -> throw new NotMetConditions("Неправильный параметр запроса (state)");
        };
    }

    private void correctBookingTime(BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new NotMetConditions("Неправильное время бронирования");
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + bookingId + " не существует"));
    }
}
