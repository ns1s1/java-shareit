package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDto userCreateDto;
    private UserResponseDto userResponseDto;

    private User user;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto("User", "user@mail.ru");

        user = new User(1L, "User", "user1@mail.ru");

        userResponseDto = new UserResponseDto(1L, "user@mail.ru", "User");
    }

    @Test
    void testCreateUser() {
        when(userMapper.convertToUser(userCreateDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto userResponseTest = userService.create(userCreateDto);

        assertEquals(userResponseTest, userResponseDto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser() {
        user.setName("new");
        UserResponseDto updatedUser = new UserResponseDto(1L, "new", "user@mail.ru");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToUserResponse(user)).thenReturn(updatedUser);

        UserResponseDto actualUser = userService.update(userResponseDto.getId(), user);

        assertEquals(updatedUser, actualUser);
    }

    @Test
    void testGetByIdCorrect() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.convertToUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto actualUser = userService.getById(user.getId());

        assertEquals(userResponseDto, actualUser);
    }

    @Test
    void testGetUncorrectedById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.convertToUserResponse(user)).thenReturn(userResponseDto);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getById(2L));

        assertEquals(notFoundException.getMessage(), "Пользователь с данным id не найден");
    }

    @Test
    void testGetAllUsersCorrect() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.convertToUserResponse(user)).thenReturn(userResponseDto);

        List<UserResponseDto> users = userService.getAll();

        verify(userRepository, times(1)).findAll();
        assertEquals(1, users.size());
        assertEquals(userResponseDto, users.get(0));
    }

    @Test
    void testDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}