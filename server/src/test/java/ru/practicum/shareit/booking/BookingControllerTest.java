package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingResponseDto makeResponse(Long id, Long itemId, String itemName,
                                            Long bookerId, String bookerName, BookingStatus status) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        return new BookingResponseDto(id, start, end, new ItemDto(itemId, null,
                itemName, "Описание", true, null),
                new UserDto(bookerId, bookerName, bookerName.toLowerCase() + "@mail.com"), status);
    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        BookingDto input = new BookingDto(null, 2L, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), null, null);
        BookingResponseDto output = makeResponse(10L, 2L, "Дрель",
                5L, "Иван", BookingStatus.APPROVED);

        Mockito.when(bookingService.createBooking(any(BookingDto.class), eq(5L)))
                .thenReturn(output);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.item.id", is(2)))
                .andExpect(jsonPath("$.item.name", is("Дрель")))
                .andExpect(jsonPath("$.booker.id", is(5)))
                .andExpect(jsonPath("$.booker.name", is("Иван")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingResponseDto response = makeResponse(11L, 3L, "Велосипед",
                6L, "Петр", BookingStatus.APPROVED);

        Mockito.when(bookingService.approveBooking(6L, 11L, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/11")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 6L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.name", is("Велосипед")));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        BookingResponseDto response = makeResponse(12L, 4L, "Книга",
                7L, "Сергей", BookingStatus.REJECTED);

        Mockito.when(bookingService.getBookingById(7L, 12L)).thenReturn(response);

        mockMvc.perform(get("/bookings/12")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(12)))
                .andExpect(jsonPath("$.item.name", is("Книга")))
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    void getAllBookingByUser_shouldReturnList() throws Exception {
        List<BookingResponseDto> list = List.of(
                makeResponse(13L, 5L, "Ноутбук", 8L,
                        "Анна", BookingStatus.WAITING));

        Mockito.when(bookingService.getAllBookingByUser(8L, "ALL")).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 8L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(13)))
                .andExpect(jsonPath("$[0].item.name", is("Ноутбук")))
                .andExpect(jsonPath("$[0].status", is("WAITING")));
    }

    @Test
    void getAllBookingByOwner_shouldReturnList() throws Exception {
        List<BookingResponseDto> list = List.of(
                makeResponse(14L, 6L, "Проектор", 9L,
                        "Мария", BookingStatus.APPROVED));

        Mockito.when(bookingService.getAllBookingByOwner(9L, "ALL")).thenReturn(list);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 9L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(14)))
                .andExpect(jsonPath("$[0].item.name", is("Проектор")))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }
}
