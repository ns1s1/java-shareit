package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = convertToUser(userDto);
        if (checkUserWithEmail(user.getId(), user.getEmail())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }

        return convertToUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        if (checkUserWithEmail(userId, userDto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }

        User updatedUser = userRepository.getById(userId);
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }

        return convertToUserDto(userRepository.update(updatedUser));
    }

    @Override
    public UserDto getById(Long userId) {
        return convertToUserDto(userRepository.getById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    private User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private boolean checkUserWithEmail(long id, String email) {
        return userRepository.getAll().stream()
                .filter(user -> user.getId() != id)
                .map(User::getEmail)
                .anyMatch(e -> e.equals(email));
    }
}
