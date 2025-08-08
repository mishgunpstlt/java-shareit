package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserDto user = new UserDto(101L, "John", "john@example.com");
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(101)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserDto user = new UserDto(102L, "Mary", "mary@example.com");
        Mockito.when(userService.getUserById(102L)).thenReturn(user);

        mockMvc.perform(get("/users/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(102)))
                .andExpect(jsonPath("$.name", is("Mary")))
                .andExpect(jsonPath("$.email", is("mary@example.com")));
    }

    @Test
    void addUser_shouldReturnCreatedUser() throws Exception {
        UserDto input = new UserDto(null, "Alice", "alice@mail.com");
        UserDto created = new UserDto(103L, "Alice", "alice@mail.com");

        Mockito.when(userService.addUser(any(UserDto.class))).thenReturn(created);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(103)))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@mail.com")));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpdatingUserDto input = new UpdatingUserDto("NewName", "newemail@mail.com");
        UserDto updated = new UserDto(104L, "NewName", "newemail@mail.com");

        Mockito.when(userService.updateUser(eq(input), eq(104L))).thenReturn(updated);

        mockMvc.perform(patch("/users/104")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(104)))
                .andExpect(jsonPath("$.name", is("NewName")))
                .andExpect(jsonPath("$.email", is("newemail@mail.com")));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/105"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(105L);
    }
}
