package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequest_shouldReturnSavedRequest() throws Exception {
        Instant createdTime = Instant.parse("2025-08-08T10:00:00Z");
        ItemRequestDto dto = new ItemRequestDto(101L, "Нужен молоток", createdTime, 201L);

        given(itemRequestService.addItemRequest(any(ItemRequestDto.class), eq(1L)))
                .willReturn(dto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.description").value("Нужен молоток"))
                .andExpect(jsonPath("$.created").value(createdTime.toString()))
                .andExpect(jsonPath("$.requesterId").value(201));
    }

    @Test
    void getAllRequestsByUser_shouldReturnList() throws Exception {
        Instant createdTime = Instant.parse("2025-08-08T11:00:00Z");
        ItemRequestResponse response = new ItemRequestResponse(102L, "Нужен молоток", createdTime, List.of());

        given(itemRequestService.getAllRequestsByUser(1L))
                .willReturn(List.of(response));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(102))
                .andExpect(jsonPath("$[0].description").value("Нужен молоток"))
                .andExpect(jsonPath("$[0].created").value(createdTime.toString()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items").isEmpty());
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        Instant createdTime = Instant.parse("2025-08-08T12:00:00Z");
        ItemRequestResponse response = new ItemRequestResponse(103L, "Нужна дрель", createdTime, List.of());

        given(itemRequestService.getAllRequests(1L))
                .willReturn(List.of(response));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(103))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].created").value(createdTime.toString()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items").isEmpty());
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        Instant createdTime = Instant.parse("2025-08-08T13:00:00Z");
        ItemRequestResponse response = new ItemRequestResponse(104L, "Нужен велосипед", createdTime, List.of());

        given(itemRequestService.getRequestById(104L))
                .willReturn(response);

        mockMvc.perform(get("/requests/104"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(104))
                .andExpect(jsonPath("$.description").value("Нужен велосипед"))
                .andExpect(jsonPath("$.created").value(createdTime.toString()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
