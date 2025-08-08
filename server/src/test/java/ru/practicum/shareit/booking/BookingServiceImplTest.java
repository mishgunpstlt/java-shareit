package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        booker = new User(null, "Booker", "booker@mail.com");
        userRepository.save(owner);
        userRepository.save(booker);

        item = new Item(null, owner.getId(), "Drill", "Powerful drill", true, null);
        itemRepository.save(item);
    }

    @Test
    void createBooking_shouldCreateBooking() {
        BookingDto bookingDto = new BookingDto(null,
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null
        );

        BookingResponseDto response = bookingService.createBooking(bookingDto, booker.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(response.getItem().getId()).isEqualTo(item.getId());
        assertThat(response.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createBooking_withUnavailableItem_shouldThrow() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, booker.getId()))
                .isInstanceOf(NotAvailableException.class);
    }

    @Test
    void approveBooking_asOwner_shouldChangeStatus() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());
        BookingResponseDto approved = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_notOwner_shouldThrow() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), booking.getId(), true))
                .isInstanceOf(NotMetConditions.class);
    }

    @Test
    void getBookingById_asBookerOrOwner_shouldReturnBooking() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());

        BookingResponseDto byBooker = bookingService.getBookingById(booker.getId(), booking.getId());
        BookingResponseDto byOwner = bookingService.getBookingById(owner.getId(), booking.getId());

        assertThat(byBooker).isNotNull();
        assertThat(byOwner).isNotNull();
        assertThat(byBooker.getId()).isEqualTo(booking.getId());
        assertThat(byOwner.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingById_notBookerOrOwner_shouldThrow() {
        User otherUser = new User(null, "Other", "other@mail.com");
        userRepository.save(otherUser);

        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());

        assertThatThrownBy(() -> bookingService.getBookingById(otherUser.getId(), booking.getId()))
                .isInstanceOf(NotMetConditions.class);
    }

    @Test
    void getAllBookingByUser_shouldReturnBookings() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);
        bookingService.createBooking(bookingDto, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getAllBookingByUser(booker.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
    }

    @Test
    void getAllBookingByOwner_shouldReturnBookings() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);
        bookingService.createBooking(bookingDto, booker.getId());

        List<BookingResponseDto> bookings = bookingService.getAllBookingByOwner(owner.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
    }

    @Test
    void approveBooking_asOwner_rejectBooking() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());
        BookingResponseDto rejected = bookingService.approveBooking(owner.getId(), booking.getId(), false);

        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_withNonWaitingStatus_shouldThrow() {
        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());
        bookingService.approveBooking(owner.getId(), booking.getId(), true); // status -> APPROVED

        assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), booking.getId(), true))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Статус бронирования не в статусе ожидания");
    }

    @Test
    void getBookingById_notOwnerOrBooker_shouldThrow() {
        User otherUser = new User(null, "Other", "other@mail.com");
        userRepository.save(otherUser);

        BookingDto bookingDto = new BookingDto(null, item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null);

        BookingResponseDto booking = bookingService.createBooking(bookingDto, booker.getId());

        assertThatThrownBy(() -> bookingService.getBookingById(otherUser.getId(), booking.getId()))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Просмотреть бронирование может либо автор бронирования, либо владелец вещи");
    }

    @Test
    void getAllBookingByUser_withInvalidState_shouldThrow() {
        assertThatThrownBy(() -> bookingService.getAllBookingByUser(booker.getId(), "INVALID"))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Неправильный параметр запроса");
    }

    @Test
    void getAllBookingByOwner_withInvalidState_shouldThrow() {
        assertThatThrownBy(() -> bookingService.getAllBookingByOwner(owner.getId(), "INVALID"))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Неправильный параметр запроса");
    }

    @Test
    void getAllBookingByOwner_allStates_shouldReturnListOrThrow() {
        List<String> states = List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");

        for (String state : states) {
            List<BookingResponseDto> result = bookingService.getAllBookingByOwner(owner.getId(), state);
            assertThat(result).isNotNull();
        }
    }

    @Test
    void getAllBookingByUser_allStates_shouldReturnListOrThrow() {
        List<String> states = List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");

        for (String state : states) {
            List<BookingResponseDto> result = bookingService.getAllBookingByUser(booker.getId(), state);
            assertThat(result).isNotNull();
        }
    }
}
