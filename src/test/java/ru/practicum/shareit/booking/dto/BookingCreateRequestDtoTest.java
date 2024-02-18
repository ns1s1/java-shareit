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
class BookingCreateRequestDtoTest {

    @Autowired
    JacksonTester<BookingCreateRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialize() {
        BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto();
        bookingCreateRequestDto.setItemId(1L);
        bookingCreateRequestDto.setStart(LocalDateTime.now());
        bookingCreateRequestDto.setEnd(LocalDateTime.now());

        JsonContent<BookingCreateRequestDto> result = jacksonTester.write(bookingCreateRequestDto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
    }

}