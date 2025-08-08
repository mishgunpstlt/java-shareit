package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.IsntOwnerException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner", "owner@mail.com");
        booker = new User(null, "Booker", "booker@mail.com");
        userRepository.save(owner);
        userRepository.save(booker);
    }

    @Test
    void addItem_shouldSaveItem() {
        ItemDto dto = new ItemDto(null, owner.getId(), "Drill", "Powerful drill", true, null);

        ItemDto saved = itemService.addItem(dto, owner.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Drill");

        Item itemFromDb = itemRepository.findById(saved.getId()).orElseThrow();
        assertThat(itemFromDb.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItem_ownerCanUpdate() {
        Item item = new Item(null, owner.getId(), "Drill", "Old desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateDto = new UpdatingItemDto("New Drill", "New desc", false);
        ItemDto updated = itemService.updateItem(updateDto, item.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("New Drill");
        assertThat(updated.getDescription()).isEqualTo("New desc");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateItem_notOwner_shouldThrow() {
        Item item = new Item(null, owner.getId(), "Drill", "Desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateDto = new UpdatingItemDto("New", "New", true);

        assertThatThrownBy(() -> itemService.updateItem(updateDto, item.getId(), booker.getId()))
                .isInstanceOf(IsntOwnerException.class);
    }

    @Test
    void getItemById_existing_returnsItemWithComments() {
        Item item = new Item(null, owner.getId(), "Drill", "Desc", true, null);
        itemRepository.save(item);

        Comment comment = new Comment(null, "Good drill", Instant.now(), booker.getId(), item.getId());
        commentRepository.save(comment);

        ItemBookingTimeDto itemDto = itemService.getItemById(item.getId());

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getComments().getFirst().getText()).isEqualTo("Good drill");
    }

    @Test
    void getItemById_notExisting_shouldThrow() {
        assertThatThrownBy(() -> itemService.getItemById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllItemsByUserId_returnsItemsWithBookingsAndComments() {
        Item item = new Item(null, owner.getId(), "Drill", "Desc", true, null);
        itemRepository.save(item);

        Booking booking = new Booking(null, item, Instant.now().minusSeconds(3600 * 24 * 2),
                Instant.now().minusSeconds(3600 * 24), booker, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Comment comment = new Comment(null, "Nice", Instant.now(), booker.getId(), item.getId());
        commentRepository.save(comment);

        List<ItemBookingTimeDto> items = itemService.getAllItemsByUserId(owner.getId());

        assertThat(items).hasSize(1);
        ItemBookingTimeDto itemDto = items.get(0);
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getLastBooking()).isNotNull();
    }

    @Test
    void searchItemsByText_returnsMatchingItems() {
        Item item = new Item(null, owner.getId(), "Drill", "Powerful", true, null);
        itemRepository.save(item);

        List<ItemDto> result = itemService.searchItemsByText("drill");
        assertThat(result).hasSize(1);

        result = itemService.searchItemsByText("pow");
        assertThat(result).hasSize(1);

        result = itemService.searchItemsByText("");
        assertThat(result).isEmpty();
    }

    @Test
    void isAvailable_throwsIfUnavailable() {
        ItemDto itemDto = new ItemDto(1L, owner.getId(), "Item", "Desc", false, null);
        ItemBookingTimeDto dto = ItemMapper.toItemBookingDto(itemDto);

        assertThatThrownBy(() -> itemService.isAvailable(dto))
                .isInstanceOf(NotAvailableException.class);
    }

    @Test
    void addComment_validBooker_savesComment() {
        Item item = new Item(null, owner.getId(), "Drill", "Desc", true, null);
        itemRepository.save(item);

        Booking booking = new Booking(null, item, Instant.now().minusSeconds(3600 * 24 * 5),
                Instant.now().minusSeconds(3600 * 24), booker, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Great!", null, null, null);
        CommentAuthorNameDto savedComment = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(savedComment.getText()).isEqualTo("Great!");
        assertThat(savedComment.getAuthorName()).isEqualTo(booker.getName());
    }

    @Test
    void addComment_allConditionsMet_shouldSaveComment() {
        Item item = new Item(null, owner.getId(), "Item1", "Desc", true, null);
        itemRepository.save(item);

        Booking booking = new Booking(null, item, Instant.now().minusSeconds(3600 * 24 * 3),
                Instant.now().minusSeconds(3600 * 24 * 2), booker, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Nice item!", null, null, null);
        CommentAuthorNameDto savedComment = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(savedComment.getText()).isEqualTo("Nice item!");
        assertThat(savedComment.getAuthorName()).isEqualTo(booker.getName());
    }


    @Test
    void addComment_bookingNotEnded_shouldThrow() {
        Item item = new Item(null, owner.getId(), "Item3", "Desc", true, null);
        itemRepository.save(item);

        Booking booking = new Booking(null, item, Instant.now().minusSeconds(3600 * 24),
                Instant.now().plusSeconds(3600 * 24), booker, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Test", null, null, null);

        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), commentDto))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Комментарий может оставить только пользователь");
    }

    @Test
    void addComment_bookingStatusNotApproved_shouldThrow() {
        Item item = new Item(null, owner.getId(), "Item4", "Desc", true, null);
        itemRepository.save(item);

        Booking booking = new Booking(null, item, Instant.now().minusSeconds(3600 * 24 * 3),
                Instant.now().minusSeconds(3600 * 24 * 2), booker, BookingStatus.REJECTED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Test", null, null, null);

        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), commentDto))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Комментарий может оставить только пользователь");
    }


    @Test
    void getAllItemsByUserId_onlyPastBookings_setsLastBookingOnly() {
        Item item = new Item(null, owner.getId(), "PastItem", "Desc", true, null);
        itemRepository.save(item);

        Instant now = Instant.now();
        Booking pastBooking1 = new Booking(null, item, now.minusSeconds(3600 * 24 * 3),
                now.minusSeconds(3600 * 24 * 2), booker, BookingStatus.APPROVED);
        Booking pastBooking2 = new Booking(null, item, now.minusSeconds(3600 * 24 * 5),
                now.minusSeconds(3600 * 24 * 4), booker, BookingStatus.APPROVED);
        bookingRepository.saveAll(List.of(pastBooking1, pastBooking2));

        List<ItemBookingTimeDto> items = itemService.getAllItemsByUserId(owner.getId());

        ItemBookingTimeDto dto = items.stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking()).isEqualTo(pastBooking1.getStart());
        assertThat(dto.getNextBooking()).isNull();
    }

    @Test
    void getAllItemsByUserId_onlyFutureBookings_setsNextBookingOnly() {
        Item item = new Item(null, owner.getId(), "FutureItem", "Desc", true, null);
        itemRepository.save(item);

        Instant now = Instant.now();
        Booking futureBooking1 = new Booking(null, item, now.plusSeconds(3600 * 24 * 2),
                now.plusSeconds(3600 * 24 * 3), booker, BookingStatus.APPROVED);
        Booking futureBooking2 = new Booking(null, item, now.plusSeconds(3600 * 24 * 4),
                now.plusSeconds(3600 * 24 * 5), booker, BookingStatus.APPROVED);
        bookingRepository.saveAll(List.of(futureBooking1, futureBooking2));

        List<ItemBookingTimeDto> items = itemService.getAllItemsByUserId(owner.getId());

        ItemBookingTimeDto dto = items.stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isEqualTo(futureBooking1.getStart());
    }

    @Test
    void getAllItemsByUserId_pastAndFutureBookings_setsLastAndNextBooking() {
        Item item = new Item(null, owner.getId(), "MixedItem", "Desc", true, null);
        itemRepository.save(item);

        Instant now = Instant.now();
        Booking pastBooking = new Booking(null, item, now.minusSeconds(3600 * 24 * 3),
                now.minusSeconds(3600 * 24 * 2), booker, BookingStatus.APPROVED);
        Booking futureBooking = new Booking(null, item, now.plusSeconds(3600 * 24 * 2),
                now.plusSeconds(3600 * 24 * 3), booker, BookingStatus.APPROVED);
        bookingRepository.saveAll(List.of(pastBooking, futureBooking));

        List<ItemBookingTimeDto> items = itemService.getAllItemsByUserId(owner.getId());

        ItemBookingTimeDto dto = items.stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getLastBooking()).isEqualTo(pastBooking.getStart());
        assertThat(dto.getNextBooking()).isEqualTo(futureBooking.getStart());
    }

    @Test
    void getAllItemsByUserId_bookingsWithSameStartDate_correctLastAndNextBooking() {
        Item item = new Item(null, owner.getId(), "SameStart", "Desc", true, null);
        itemRepository.save(item);

        Instant now = Instant.now();
        Booking pastBooking1 = new Booking(null, item, now.minusSeconds(3600),
                now.plusSeconds(3600), booker, BookingStatus.APPROVED);
        Booking pastBooking2 = new Booking(null, item, now.minusSeconds(3600),
                now.plusSeconds(7200), booker, BookingStatus.APPROVED);
        Booking futureBooking = new Booking(null, item, now.plusSeconds(3600),
                now.plusSeconds(10800), booker, BookingStatus.APPROVED);

        bookingRepository.saveAll(List.of(pastBooking1, pastBooking2, futureBooking));

        List<ItemBookingTimeDto> items = itemService.getAllItemsByUserId(owner.getId());

        ItemBookingTimeDto dto = items.stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(dto.getLastBooking()).isEqualTo(pastBooking1.getStart());

        assertThat(dto.getNextBooking()).isEqualTo(futureBooking.getStart());
    }

    @Test
    void getItem_nonExistingId_shouldThrowNotFoundException() {
        Long nonExistingId = 99999L;
        CommentDto commentDto = new CommentDto(null, "Great!", null, null, null);
        assertThatThrownBy(() -> itemService.addComment(owner.getId(), nonExistingId, commentDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь c id=" + nonExistingId + " не существует");
    }

    @Test
    void updateItem_allFieldsUpdated_whenValid() {
        Item item = new Item(null, owner.getId(), "Drill", "Old desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateDto = new UpdatingItemDto("New Drill", "New desc", false);
        ItemDto updated = itemService.updateItem(updateDto, item.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("New Drill");
        assertThat(updated.getDescription()).isEqualTo("New desc");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateItem_nameNullOrBlank_shouldNotChangeName() {
        Item item = new Item(null, owner.getId(), "Drill", "Old desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateNull = new UpdatingItemDto(null, "New desc", false);
        ItemDto updatedNull = itemService.updateItem(updateNull, item.getId(), owner.getId());
        assertThat(updatedNull.getName()).isEqualTo("Drill"); // имя не меняется
        assertThat(updatedNull.getDescription()).isEqualTo("New desc");
        assertThat(updatedNull.getAvailable()).isFalse();

        UpdatingItemDto updateBlank = new UpdatingItemDto("   ", "Desc2", true);
        ItemDto updatedBlank = itemService.updateItem(updateBlank, item.getId(), owner.getId());
        assertThat(updatedBlank.getName()).isEqualTo("Drill"); // имя не меняется
        assertThat(updatedBlank.getDescription()).isEqualTo("Desc2");
        assertThat(updatedBlank.getAvailable()).isTrue();
    }

    @Test
    void updateItem_descriptionNullOrBlank_shouldNotChangeDescription() {
        Item item = new Item(null, owner.getId(), "Drill", "Old desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateNull = new UpdatingItemDto("New name", null, false);
        ItemDto updatedNull = itemService.updateItem(updateNull, item.getId(), owner.getId());
        assertThat(updatedNull.getDescription()).isEqualTo("Old desc"); // описание не меняется
        assertThat(updatedNull.getName()).isEqualTo("New name");
        assertThat(updatedNull.getAvailable()).isFalse();

        UpdatingItemDto updateBlank = new UpdatingItemDto("New name 2", "   ", true);
        ItemDto updatedBlank = itemService.updateItem(updateBlank, item.getId(), owner.getId());
        assertThat(updatedBlank.getDescription()).isEqualTo("Old desc"); // описание не меняется
        assertThat(updatedBlank.getName()).isEqualTo("New name 2");
        assertThat(updatedBlank.getAvailable()).isTrue();
    }

    @Test
    void updateItem_availableNull_shouldNotChangeAvailable() {
        Item item = new Item(null, owner.getId(), "Drill", "Desc", true, null);
        itemRepository.save(item);

        UpdatingItemDto updateDto = new UpdatingItemDto("Name", "Description", null);
        ItemDto updated = itemService.updateItem(updateDto, item.getId(), owner.getId());

        assertThat(updated.getAvailable()).isTrue();
        assertThat(updated.getName()).isEqualTo("Name");
        assertThat(updated.getDescription()).isEqualTo("Description");
    }

}