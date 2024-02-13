package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    ItemResponseDto itemResponseDto;

    ItemCreateRequestDto itemCreateRequestDto;

    ItemWitchBookingResponseDto itemWitchBookingResponseDto;

    CommentCreateRequestDto commentCreateRequestDto;

    CommentResponseDto commentResponseDto;

    private static final String USER_ID_HEAD = "X-Sharer-User-Id";


    @BeforeEach
    public void init() {
        itemCreateRequestDto = ItemCreateRequestDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemWitchBookingResponseDto = new ItemWitchBookingResponseDto();

        commentCreateRequestDto = new CommentCreateRequestDto();

        itemResponseDto = new ItemResponseDto();

        commentResponseDto = new CommentResponseDto();
    }

    @SneakyThrows
    @Test
    void testCreateCorrectItem() {
        when(itemService.create(any(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful());

        verify(itemService, times(1)).create(any(), any());
    }

    @SneakyThrows
    @Test
    void testCreateItemWithTheWrongName() {
        itemCreateRequestDto.setName(null);
        when(itemService.create(any(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(itemService, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void testCreateItemWithTheWrongDescription() {
        itemCreateRequestDto.setDescription(null);
        when(itemService.create(any(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(itemService, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void testCreateItemWithTheWrongAvaiable() {
        itemCreateRequestDto.setAvailable(null);
        when(itemService.create(any(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());

        verify(itemService, never()).create(any(), any());
    }

    @SneakyThrows
    @Test
    void updateCorrectItem() {
        ItemUpdateRequestDto itemUpdateRequestDto = new ItemUpdateRequestDto();
        itemUpdateRequestDto.setName("update");
        itemUpdateRequestDto.setDescription("update description");
        itemUpdateRequestDto.setAvailable(true);
        when(itemService.update(any(), any(), any())).thenReturn(itemResponseDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEAD, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful());

        verify(itemService, times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserIdCorrect() {
        when(itemService.getAllItemsByUserId(any())).thenReturn(List.of(itemWitchBookingResponseDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful());

        verify(itemService, times(1)).getAllItemsByUserId(any());
    }

    @SneakyThrows
    @Test
    void searchItemCorrect() {
        when(itemService.searchItem(any())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().is2xxSuccessful());

        verify(itemService, times(1)).searchItem(any());
    }

    @SneakyThrows
    @Test
    void getByIdCorrect() {
        when(itemService.getById(any(), any())).thenReturn(itemWitchBookingResponseDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header(USER_ID_HEAD, 1))
                .andExpect(status().is2xxSuccessful());

        verify(itemService, times(1)).getById(any(), any());
    }

    @SneakyThrows
    @Test
    void createCommentCorrect() {
        commentCreateRequestDto.setText("text");
        when(itemService.createComment(any(), any(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID_HEAD, 1)
                        .content(objectMapper.writeValueAsString(commentCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful());
        verify(itemService, times(1)).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createCommentWithBlankText() {
        commentCreateRequestDto.setText("");
        when(itemService.createComment(any(), any(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID_HEAD, 1)
                        .content(objectMapper.writeValueAsString(commentCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());
        verify(itemService, never()).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createCommentWithAuthorOrItemNotFound() {
        commentCreateRequestDto.setText("text");
        when(itemService.createComment(any(), any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID_HEAD, 1)
                        .content(objectMapper.writeValueAsString(commentCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());
        verify(itemService).createComment(any(), any(), any());
    }
}