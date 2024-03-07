package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class ItemWitchBookingResponseDtoTest {

    @Autowired
    JacksonTester<ItemWitchBookingResponseDto> jacksonTester;

    @Test
    @SneakyThrows
    void testSerialize() {
        BookingForItemResponseDto bookingForItemResponseDto = BookingForItemResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .bookerId(1L)
                .build();

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();

        ItemWitchBookingResponseDto itemWitchBookingResponseDto = ItemWitchBookingResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(bookingForItemResponseDto)
                .nextBooking(bookingForItemResponseDto)
                .comments(List.of(commentResponseDto))
                .build();

        JsonContent<ItemWitchBookingResponseDto> result = jacksonTester.write(itemWitchBookingResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.comments[0].id");
        assertThat(result).hasJsonPath("$.comments[0].text");
        assertThat(result).hasJsonPath("$.comments[0].authorName");
        assertThat(result).hasJsonPath("$.comments[0].created");
        assertThat(result).hasJsonPath("$.lastBooking.id");
        assertThat(result).hasJsonPath("$.lastBooking.bookerId");
        assertThat(result).hasJsonPath("$.lastBooking.start");
        assertThat(result).hasJsonPath("$.lastBooking.end");
        assertThat(result).hasJsonPath("$.nextBooking.id");
        assertThat(result).hasJsonPath("$.nextBooking.bookerId");
        assertThat(result).hasJsonPath("$.nextBooking.start");
        assertThat(result).hasJsonPath("$.nextBooking.end");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).hasJsonPathValue("$.comments[0].created");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.lastBooking.start");
        assertThat(result).hasJsonPathValue("$.lastBooking.end");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.nextBooking.start");
        assertThat(result).hasJsonPathValue("$.nextBooking.end");

    }
}