package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemCreateRequestDtoTest {

    @Autowired
    JacksonTester<ItemCreateRequestDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemCreateRequestDto itemCreateRequestDto = ItemCreateRequestDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemCreateRequestDto> result = jacksonTester.write(itemCreateRequestDto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

    }

}