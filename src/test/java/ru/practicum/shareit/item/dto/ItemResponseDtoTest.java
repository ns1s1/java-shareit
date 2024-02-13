package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class ItemResponseDtoTest {

    @Autowired
    JacksonTester<ItemResponseDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .commentResponse(commentResponseDto)
                .requestId(1L)
                .build();

        JsonContent<ItemResponseDto> result = jacksonTester.write(itemResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.commentResponse.id");
        assertThat(result).hasJsonPath("$.commentResponse.text");
        assertThat(result).hasJsonPath("$.commentResponse.authorName");
        assertThat(result).hasJsonPath("$.commentResponse.created");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.commentResponse.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.commentResponse.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.commentResponse.authorName")
                .isEqualTo("author");
        assertThat(result).hasJsonPathValue("$.commentResponse.created");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);


    }

}