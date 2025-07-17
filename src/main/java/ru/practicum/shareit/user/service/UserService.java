package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UpdatingUserDto userDto, Long userId);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);

}
