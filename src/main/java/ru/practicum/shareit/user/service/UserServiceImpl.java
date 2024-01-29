package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {

        return userMapper.convertToUserResponse(userRepository.save(userMapper.convertToUser(userCreateDto)));
    }

    @Override
    public UserResponseDto update(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c данным id не найден"));

        checkUserWithEmail(userId, user.getEmail());
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }

        return userMapper.convertToUserResponse(userRepository.save((user)));
    }

    @Override
    public UserResponseDto getById(Long userId) {
        return userMapper.convertToUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не найден")));
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean checkUserWithEmail(Long id, String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getId() != id)
                .map(User::getEmail)
                .anyMatch(e -> e.equals(email));
    }
}
