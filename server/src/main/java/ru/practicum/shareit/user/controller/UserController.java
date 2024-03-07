package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getById(@PathVariable("userId") Long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserResponseDto create(@RequestBody @Valid UserCreateDto user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto update(@PathVariable("userId") Long userId,
                                  @RequestBody User userUpdateDto) {

        return userService.update(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }
}
