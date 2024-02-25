package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("description");

        JsonContent<ItemRequestCreateDto> result = jacksonTester.write(itemRequestCreateDto);
        assertThat(result).hasJsonPath("$.description");

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }
}