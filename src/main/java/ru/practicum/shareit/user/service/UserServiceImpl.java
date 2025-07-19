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
import java.util.Optional;

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
        existEmail(userDto.getEmail());
        User user = UserMapper.toEntity(userDto);
        user = userRepository.addUser(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(UpdatingUserDto userDto, Long userId) {
        UserDto oldUser = getUserById(userId);
        if (!oldUser.getEmail().equals(userDto.getEmail())) {
            existEmail(userDto.getEmail());
        }
        User user = UserMapper.toUpdatingUser(userDto);
        user = userRepository.updateUser(user, userId);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        existUser(userId);
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь c id=" + userId + " не существует");
        }
        return UserMapper.toDto(user.get());
    }

    private void existEmail(String email) {
        if (userRepository.existEmail(email)) {
            throw new ExistingEmailException("Пользователь с email=" + email + " уже существует");
        }
    }

    private void existUser(Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь c id=" + userId + " не существует");
        }
    }
}
