package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemUpdateRequestDtoTest {

    @Autowired
    JacksonTester<ItemUpdateRequestDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemUpdateRequestDto itemUpdateRequestDto = new ItemUpdateRequestDto();
        itemUpdateRequestDto.setId(1L);
        itemUpdateRequestDto.setName("name");
        itemUpdateRequestDto.setDescription("description");
        itemUpdateRequestDto.setAvailable(true);

        JsonContent<ItemUpdateRequestDto> result = jacksonTester.write(itemUpdateRequestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

}