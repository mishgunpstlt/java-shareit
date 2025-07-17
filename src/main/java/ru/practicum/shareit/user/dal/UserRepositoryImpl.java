package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId);
        users.put(user.getId(), user);
        nextId++;
        return user;
    }

    @Override
    public User updateUser(User user, Long userId) {
        User newUser = users.get(userId);
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        users.put(userId, newUser);
        return newUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }
}
