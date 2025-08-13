package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistingEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UpdatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto existingUser;

    @BeforeEach
    void setup() {
        existingUser = userService.addUser(new UserDto(null, "John", "john@mail.com"));
    }

    @Test
    void addUser_shouldPersistUser() {

        assertThat(existingUser.getId()).isNotNull();
        assertThat(existingUser.getName()).isEqualTo("John");
        assertThat(userRepository.findById(existingUser.getId())).isPresent();
    }

    @Test
    void addUser_withDuplicateEmail_shouldThrow() {
        assertThatThrownBy(() ->
                userService.addUser(new UserDto(null, "Another John", "john@mail.com"))
        ).isInstanceOf(ExistingEmailException.class);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        userService.addUser(new UserDto(null, "Alice", "alice@mail.com"));

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void updateUser_withNullAndBlankFields_shouldNotChangeThem() {
        UpdatingUserDto update = new UpdatingUserDto("NewName", "new@mail.com");
        userService.updateUser(update, existingUser.getId());

        UpdatingUserDto updateNulls = new UpdatingUserDto(null, "   ");
        UserDto updated = userService.updateUser(updateNulls, existingUser.getId());

        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrow() {
        userService.addUser(new UserDto(null, "Alice", "alice@mail.com"));

        UpdatingUserDto update = new UpdatingUserDto(null, "alice@mail.com");

        assertThatThrownBy(() -> userService.updateUser(update, existingUser.getId()))
                .isInstanceOf(ExistingEmailException.class);
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto found = userService.getUserById(existingUser.getId());

        assertThat(found).usingRecursiveComparison().isEqualTo(existingUser);
    }

    @Test
    void getUserById_nonexistent_shouldThrow() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        userService.deleteUser(existingUser.getId());

        assertThat(userRepository.findById(existingUser.getId())).isEmpty();
    }

    @Test
    void updateUser_emailNotChanged_doesNotCheckExistEmail() {
        UpdatingUserDto update = new UpdatingUserDto("NewName", existingUser.getEmail());
        UserDto updated = userService.updateUser(update, existingUser.getId());

        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getEmail()).isEqualTo(existingUser.getEmail());
    }

    @Test
    void updateUser_emailNull_doesNotChangeEmail() {
        UpdatingUserDto update = new UpdatingUserDto("NewName", null);
        UserDto updated = userService.updateUser(update, existingUser.getId());

        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getEmail()).isEqualTo(existingUser.getEmail());
    }

    @Test
    void updateUser_nameNull_doesNotChangeName() {
        UpdatingUserDto update = new UpdatingUserDto(null, "newemail@mail.com");
        UserDto updated = userService.updateUser(update, existingUser.getId());

        assertThat(updated.getName()).isEqualTo(existingUser.getName());
        assertThat(updated.getEmail()).isEqualTo("newemail@mail.com");
    }
}
