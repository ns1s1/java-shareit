package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemWitchRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        ItemWitchRequest itemWitchRequest = ItemWitchRequest.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of(itemWitchRequest))
                .build();

        JsonContent<ItemRequestResponseDto> result = jacksonTester.write(itemRequestResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items[0].id");
        assertThat(result).hasJsonPath("$.items[0].name");
        assertThat(result).hasJsonPath("$.items[0].description");
        assertThat(result).hasJsonPath("$.items[0].available");
        assertThat(result).hasJsonPath("$.items[0].requestId");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getRequestId().intValue());

    }
}