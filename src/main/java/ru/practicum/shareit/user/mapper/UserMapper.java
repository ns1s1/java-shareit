package ru.practicum.shareit.user.mapper;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User convertToUser(UserCreateDto userCreateDto);

    User convertToUser(UserUpdateDto userUpdateDto);

    UserResponseDto convertToUserResponse(User user);

    List<UserResponseDto> convertToList(List<User> users);
}
