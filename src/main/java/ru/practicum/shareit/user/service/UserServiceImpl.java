package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistingEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (existEmail(userDto.getEmail())) {
            throw new ExistingEmailException("Пользователь с таким email уже существует");
        }
        User user = UserMapper.toEntity(userDto);
        user = userRepository.addUser(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(UpdatingUserDto userDto, Long userId) {
        if (existEmail(userDto.getEmail())) {
            throw new ExistingEmailException("Пользователь с таким email уже существует");
        }
        User user = UserMapper.toUpdatingUser(userDto);
        if (getUserById(userId) != null) {
            user = userRepository.updateUser(user, userId);
            return UserMapper.toDto(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не существует");
        }
        return UserMapper.toDto(user);
    }

    private boolean existEmail(String email) {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
