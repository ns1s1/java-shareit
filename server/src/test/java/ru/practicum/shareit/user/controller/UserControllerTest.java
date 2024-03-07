package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserResponseDto userResponseDto;

    private UserCreateDto userCreateDto;


    @BeforeEach
    public void init() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("user@mail.ru");
        userCreateDto.setName("user");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail(userCreateDto.getEmail());
        userResponseDto.setName(userCreateDto.getName());
    }

    @SneakyThrows
    @Test
    void testGetAllCorrect() {
        when(userService.getAll()).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful());
        verify(userService, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void testGetByIdCorrect() {
        when(userService.getById(1L)).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().is2xxSuccessful());

        verify(userService, times(1)).getById(1L);
    }

    @SneakyThrows
    @Test
    void testCreateUserCorrect() {
        when(userService.create(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())));

        verify(userService, times(1)).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void testCreateUserUncorrectedEmail() {
        userCreateDto.setEmail(" ");
        when(userService.create(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());
        verify(userService, never()).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void testCreateUserEmailIsNull() {
        userCreateDto.setEmail(null);
        when(userService.create(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError());
        verify(userService, never()).create(userCreateDto);
    }

    @SneakyThrows
    @Test
    void testUpdateUserCorrect() {
        userResponseDto.setEmail("update@mail.ru");

        when(userService.update(any(), any())).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userResponseDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.email", is("update@mail.ru")));

        verify(userService, times(1)).update(any(), any());
    }

    @Test
    @SneakyThrows
    void testDeleteUser() {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}