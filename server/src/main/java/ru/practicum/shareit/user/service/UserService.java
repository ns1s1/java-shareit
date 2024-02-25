package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto user);

    UserResponseDto update(Long userId, User user);

    UserResponseDto getById(Long userId);

    List<UserResponseDto> getAll();

    void delete(Long userId);
}
