package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user, Long userId);

    void deleteUser(Long userId);

    Optional<User> getUserById(Long userId);

    boolean existEmail(String email);
}
