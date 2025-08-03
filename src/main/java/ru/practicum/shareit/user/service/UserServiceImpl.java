package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistingEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = false)
    public UserDto addUser(UserDto userDto) {
        existEmail(userDto.getEmail());
        User user = UserMapper.toEntity(userDto);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdatingUserDto userDto, Long userId) {
        User updatingUser = UserMapper.toEntity(getUserById(userId));
        if (!updatingUser.getEmail().equals(userDto.getEmail())) {
            existEmail(userDto.getEmail());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            updatingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatingUser.setName(userDto.getName());
        }
        userRepository.save(updatingUser);
        return UserMapper.toDto(updatingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        existUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь c id=" + userId + " не существует"));
        return UserMapper.toDto(user);
    }

    @Override
    public void existUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь c id=" + userId + " не существует");
        }
    }

    private void existEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ExistingEmailException("Пользователь с email=" + email + " уже существует");
        }
    }
}
