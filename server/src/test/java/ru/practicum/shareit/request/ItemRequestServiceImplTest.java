package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotMetConditions;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(new User(null, "User1", "user1@mail.com"));
        user2 = userRepository.save(new User(null, "User2", "user2@mail.com"));
    }

    @Test
    void addItemRequest_shouldCreateRequest_whenDescriptionIsUnique() {
        ItemRequestDto dto = new ItemRequestDto(null, "Ищу дрель", Instant.now(), null);

        ItemRequestDto saved = itemRequestService.addItemRequest(dto, user1.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Ищу дрель");
        assertThat(saved.getRequesterId()).isEqualTo(user1.getId());
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void addItemRequest_shouldThrow_whenItemWithSameDescriptionExists() {
        Item item = new Item(null, user2.getId(), "Отвертка", "Отвертка с насадками", true, null);
        itemRepository.save(item);

        ItemRequestDto dto = new ItemRequestDto(null, "Отвертка", Instant.now(), null);

        assertThatThrownBy(() -> itemRequestService.addItemRequest(dto, user1.getId()))
                .isInstanceOf(NotMetConditions.class)
                .hasMessageContaining("Вещь с таким описанием существует");
    }

    @Test
    void getAllRequestsByUser_shouldReturnOnlyRequestsOfUser() {
        ItemRequestDto req1 = itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc1", Instant.now(), null), user1.getId());
        ItemRequestDto req2 = itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc2", Instant.now(), null), user1.getId());
        itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc3", Instant.now(), null), user2.getId());

        List<ItemRequestResponse> requests = itemRequestService.getAllRequestsByUser(user1.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting("description").containsExactlyInAnyOrder("desc1", "desc2");
    }

    @Test
    void getAllRequests_shouldReturnRequestsExceptUserOwn() {
        itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc1", Instant.now(), null), user1.getId());
        itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc2", Instant.now(), null), user1.getId());
        ItemRequestDto req3 = itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc3", Instant.now(), null), user2.getId());

        List<ItemRequestResponse> requests = itemRequestService.getAllRequests(user1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("desc3");
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() {
        ItemRequestDto savedReq = itemRequestService.addItemRequest(
                new ItemRequestDto(null, "desc", Instant.now(), null), user1.getId());

        Item item = new Item(null, user1.getId(), "Инструмент", "Полезный инструмент",
                true, savedReq.getId());
        itemRepository.save(item);

        ItemRequestResponse response = itemRequestService.getRequestById(savedReq.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedReq.getId());
        assertThat(response.getDescription()).isEqualTo("desc");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getId()).isEqualTo(item.getId());
    }

    @Test
    void getRequestById_shouldThrow_whenNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id=999 не найден");
    }

    @Test
    void fillRequests_shouldIncludeItems() {
        ItemRequestDto requestDto = new ItemRequestDto(null, "desc", Instant.now(), null);
        ItemRequestDto savedRequest = itemRequestService.addItemRequest(requestDto, user1.getId());

        Item item = new Item(null, user1.getId(), "Item1", "Desc", true, savedRequest.getId());
        itemRepository.save(item);

        List<ItemRequestResponse> responses = itemRequestService.getAllRequestsByUser(user1.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getItems()).hasSize(1);
        assertThat(responses.get(0).getItems().get(0).getId()).isEqualTo(item.getId());
    }
}