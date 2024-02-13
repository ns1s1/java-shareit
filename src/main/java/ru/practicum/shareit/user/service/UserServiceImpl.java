package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto userCreateDto) {
        log.debug("[UserServiceImpl][create] user = {}", userCreateDto);
        return userMapper.convertToUserResponse(userRepository.save(userMapper.convertToUser(userCreateDto)));
    }

    @Override
    @Transactional
    public UserResponseDto update(Long userId, User userUpdate) {
        log.debug("[UserServiceImpl][update] userId = {}, userUpdate = {}", userId, userUpdate);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c данным id не найден"));

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }

        return userMapper.convertToUserResponse(userRepository.save((user)));
    }

    @Override
    public UserResponseDto getById(Long userId) {
        log.debug("[UserServiceImpl][getById] userId = {}", userId);
        return userMapper.convertToUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id не найден")));
    }

    @Override
    public List<UserResponseDto> getAll() {
        log.debug("[UserServiceImpl][getAll]");
        return userRepository.findAll().stream()
                .map(userMapper::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.debug("[UserServiceImpl][delete] userId = {}", userId);
        userRepository.deleteById(userId);
    }
}
