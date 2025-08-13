package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("update Booking b set b.bookingStatus = ?2 where b.id = ?1")
    void approveBooking(Long bookingId, BookingStatus status);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 order by b.start desc")
    List<Booking> getAllBookingByBooker(Long userId);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 and " +
            "(b.start <= CURRENT_TIMESTAMP and b.end >= CURRENT_TIMESTAMP) " +
            "order by b.start desc")
    List<Booking> getCurrentBookingsByBooker(Long userId);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 and b.end <= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> getPastBookingsByBooker(Long userId);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 and b.start >= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> getFutureBookingsByBooker(Long userId);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 and b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> getWaitingBookingsByBooker(Long userId);

    @Query("select b from Booking b join fetch b.booker u where u.id = ?1 and b.bookingStatus = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getRejectedBookingsByBooker(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 order by b.start desc")
    List<Booking> getAllBookingsByOwner(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 and " +
            "(b.start <= CURRENT_TIMESTAMP and b.end >= CURRENT_TIMESTAMP) " +
            "order by b.start desc")
    List<Booking> getCurrentBookingsByOwner(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 and b.end <= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> getPastBookingsByOwner(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 and b.start >= CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> getFutureBookingsByOwner(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 and b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> getWaitingBookingsByOwner(Long userId);

    @Query("select b from Booking b join fetch b.item i where i.ownerId = ?1 and b.bookingStatus = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> getRejectedBookingsByOwner(Long userId);

    List<Booking> findAllByItemIdIn(List<Long> itemId);

}
