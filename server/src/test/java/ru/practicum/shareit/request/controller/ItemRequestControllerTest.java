package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequest itemRequest;

    private ItemRequestCreateDto itemRequestCreateDto;

    private ItemRequestResponseDto itemRequestResponseDto;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("description");
        itemRequestResponseDto = new ItemRequestResponseDto();
    }

    @SneakyThrows
    @Test
    void testCreateItemRequestCorrect() {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestResponseDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()));

        verify(itemRequestService, times(1)).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testGetItemRequestByIdCorrect() {
        when(itemRequestService.getItemRequestById(any(), any())).thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1L))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()));

        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());


    }

    @SneakyThrows
    @Test
    void testGetAllRequestsCorrect() {
        when(itemRequestService.getAllRequests(
                anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestResponseDto));
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEAD, 1L)
                .param("from", "0")
                .param("size", "10"))
                .andExpect(status().is2xxSuccessful());

        verify(itemRequestService, times(1)).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void testGetAllRequestsByUserIdCorrect() {
        when(itemRequestService.getAllRequestsByUserId(any())).thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEAD, 1L))
                .andExpect(status().is2xxSuccessful());

        verify(itemRequestService, times(1)).getAllRequestsByUserId(anyLong());
    }
}