package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") long userId) {
        return convertToUserDto(userService.getById(userId));
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        User user = convertToUser(userDto);
        return convertToUserDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") long userId,
                          @RequestBody UserDto userDto) {

        userDto.setId(userId);
        User user = convertToUser(userDto);

        return convertToUserDto(userService.update(user));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }

    private User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

}
