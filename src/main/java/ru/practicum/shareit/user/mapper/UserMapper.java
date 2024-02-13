package ru.practicum.shareit.user.mapper;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User convertToUser(UserCreateDto userCreateDto);

    UserResponseDto convertToUserResponse(User user);
}
