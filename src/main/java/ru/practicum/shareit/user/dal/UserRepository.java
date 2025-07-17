package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user, Long userId);

    void deleteUser(Long userId);

    User getUserById(Long userId);
}
