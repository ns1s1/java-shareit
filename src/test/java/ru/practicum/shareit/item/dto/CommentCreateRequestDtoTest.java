package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateRequestDtoTest {

    @Autowired
    private JacksonTester<CommentCreateRequestDto> jacksonTester;


    @Test
    @SneakyThrows
    void testSerialize() {
        CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto();
        commentCreateRequestDto.setText("text");

        JsonContent<CommentCreateRequestDto> result = jacksonTester.write(commentCreateRequestDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}

