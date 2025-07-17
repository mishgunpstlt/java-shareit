package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody @Valid UpdatingUserDto userDto, @PathVariable Long userId) {
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
