package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingForItemResponseDtoTest {
    @Autowired
    JacksonTester<BookingForItemResponseDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        BookingForItemResponseDto bookingForItemResponseDto = new BookingForItemResponseDto();
        bookingForItemResponseDto.setId(1L);
        bookingForItemResponseDto.setStart(LocalDateTime.now());
        bookingForItemResponseDto.setEnd(LocalDateTime.now());
        bookingForItemResponseDto.setBookerId(1L);

        JsonContent<BookingForItemResponseDto> result = jacksonTester.write(bookingForItemResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

}