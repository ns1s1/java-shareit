package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingCreateRequestDto bookingCreateRequestDto;

    private BookingResponseDto bookingResponseDto;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";


    @BeforeEach
    public void init() {
        bookingCreateRequestDto = new BookingCreateRequestDto();

        bookingResponseDto = new BookingResponseDto();
    }

    @SneakyThrows
    @Test
    void testCreateCorrectBooking() {
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateRequestDto.setItemId(1L);

        when(bookingService.create(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful());

        verify(bookingService, times(1)).create(any(), any());
    }

    @SneakyThrows
    @Test
    void testCreateUncorrectedStart() {
        bookingCreateRequestDto.setStart(null);
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateRequestDto.setItemId(1L);

        when(bookingService.create(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void testCreateStartOrEndInThePast() {
        bookingCreateRequestDto.setStart(LocalDateTime.now().minusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().minusHours(2));
        bookingCreateRequestDto.setItemId(1L);

        when(bookingService.create(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(any(), any());
    }


    @SneakyThrows
    @Test
    void testCreateUncorrectedEnd() {
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(null);
        bookingCreateRequestDto.setItemId(1L);

        when(bookingService.create(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(any(), any());
    }


    @SneakyThrows
    @Test
    void testCreateUncorrectedItemId() {
        bookingCreateRequestDto.setStart(LocalDateTime.now().plusHours(1));
        bookingCreateRequestDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingCreateRequestDto.setItemId(null);

        when(bookingService.create(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).create(any(), any());
    }


    @SneakyThrows
    @Test
    void testUpdateItemApprove() {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateStatus(any(), any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).updateStatus(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void testGetByIdCorrect() {
        when(bookingService.getById(any(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful());
        verify(bookingService, times(1)).getById(any(), any());
    }

    @SneakyThrows
    @Test
    void testGetByIdUnCorrect() {
        when(bookingService.getById(any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is4xxClientError());
        verify(bookingService).getById(any(), any());
    }

    @SneakyThrows
    @Test
    void testGetAllByBookerCorrect() {
        when(bookingService.getAllBookingsByUserId(any(), any(), any(), any())).thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful());
        verify(bookingService, times(1)).getAllBookingsByUserId(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void testGetAllByBookerUnCorrect() {
        when(bookingService.getAllBookingsByUserId(any(), any(), any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is4xxClientError());
        verify(bookingService).getAllBookingsByUserId(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void testGetAllByOwnerCorrect() {
        when(bookingService.getAllBookingsByOwnerId(any(), any(), any(), any())).thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful());
        verify(bookingService, times(1)).getAllBookingsByOwnerId(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void testGetAllByOwnerUnCorrect() {
        when(bookingService.getAllBookingsByOwnerId(any(), any(), any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is4xxClientError());
        verify(bookingService).getAllBookingsByOwnerId(any(), any(), any(), any());
    }


}