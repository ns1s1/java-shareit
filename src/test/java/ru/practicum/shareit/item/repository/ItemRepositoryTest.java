package ru.practicum.shareit.item.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        User user1 = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("user2@mailru")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Item item1 = Item.builder()
                .id(1L)
                .name("testItem")
                .description("description")
                .owner(user1)
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("secondTestItem")
                .description("secondDescription")
                .owner(user2)
                .available(true)
                .build();
        itemRepository.save(item1);
        itemRepository.save(item2);
    }



    @Test
    public void testSearchItemCorrect() {
        String searchString = "description";

        List<Item> foundItems = itemRepository.search(searchString);

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).extracting(Item::getDescription).containsExactlyInAnyOrder("description", "secondDescription");
    }

    @Test
    public void testSearchItemUnCorrect() {
        String searchString = "notExist";

        List<Item> foundItems = itemRepository.search(searchString);

        assertThat(foundItems).isEmpty();
    }

    @Test
    public void testSearchShouldNotReturnUnavailableItems() {
        String searchString = "second";

        List<Item> foundItems = itemRepository.search(searchString);

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("secondTestItem");
    }


    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
    }
}