package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_shouldReturnSavedItem() throws Exception {
        ItemDto dto = new ItemDto(101L, 201L, "Молоток",
                "Стальной молоток", true, null);
        given(itemService.addItem(any(ItemDto.class), eq(1L)))
                .willReturn(dto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.ownerId").value(201))
                .andExpect(jsonPath("$.name").value("Молоток"))
                .andExpect(jsonPath("$.description").value("Стальной молоток"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        UpdatingItemDto updateDto = new UpdatingItemDto("Новый молоток",
                "Обновлённое описание", false);
        ItemDto updated = new ItemDto(102L, 202L, "Новый молоток",
                "Обновлённое описание", false, null);

        given(itemService.updateItem(any(UpdatingItemDto.class), eq(102L), eq(1L)))
                .willReturn(updated);

        mockMvc.perform(patch("/items/102")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(102))
                .andExpect(jsonPath("$.ownerId").value(202))
                .andExpect(jsonPath("$.name").value("Новый молоток"))
                .andExpect(jsonPath("$.description").value("Обновлённое описание"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemBookingTimeDto item = new ItemBookingTimeDto(103L, 203L, "Молоток",
                "Описание", true, null, null, List.of());

        given(itemService.getItemById(103L)).willReturn(item);

        mockMvc.perform(get("/items/103"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(103))
                .andExpect(jsonPath("$.ownerId").value(203))
                .andExpect(jsonPath("$.name").value("Молоток"))
                .andExpect(jsonPath("$.description").value("Описание"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getAllItemsByUserId_shouldReturnList() throws Exception {
        ItemBookingTimeDto item = new ItemBookingTimeDto(104L, 204L, "Молоток",
                "Описание", true, null, null, List.of());

        given(itemService.getAllItemsByUserId(1L)).willReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(104))
                .andExpect(jsonPath("$[0].ownerId").value(204))
                .andExpect(jsonPath("$[0].name").value("Молоток"))
                .andExpect(jsonPath("$[0].description").value("Описание"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void searchItemsByText_shouldReturnList() throws Exception {
        ItemDto dto = new ItemDto(105L, 205L, "Дрель", "Мощная дрель", true, null);
        given(itemService.searchItemsByText("дрель")).willReturn(List.of(dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(105))
                .andExpect(jsonPath("$[0].ownerId").value(205))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Мощная дрель"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void addComment_shouldReturnSavedComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Отличная вещь", null, 301L, 106L);
        CommentAuthorNameDto savedComment = new CommentAuthorNameDto(401L, "Отличная вещь",
                Instant.parse("2025-08-08T12:00:00Z"), "Иван", 106L
        );

        given(itemService.addComment(eq(1L), eq(106L), any(CommentDto.class)))
                .willReturn(savedComment);

        mockMvc.perform(post("/items/106/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(401))
                .andExpect(jsonPath("$.text").value("Отличная вещь"))
                .andExpect(jsonPath("$.authorName").value("Иван"))
                .andExpect(jsonPath("$.itemId").value(106));
    }
}
