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
class CommentResponseDtoTest {

    @Autowired
    JacksonTester<CommentResponseDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerialize() {
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentResponseDto> result = jacksonTester.write(commentResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(result).hasJsonPathValue("$.created");
    }


}