package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


@JsonTest
class UserCreateDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("user");
        userCreateDto.setEmail("user@mail.ru");

        JsonContent<UserCreateDto> result = jacksonTester.write(userCreateDto);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.ru");
    }
}