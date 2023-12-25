package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private long id = 0;
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (checkUserWithEmail(user.getId(), user.getEmail())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }

        user.setId(generatedId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        if (checkUserWithEmail(user.getId(), user.getEmail())) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }

        User updatedUser = users.get(user.getId());
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        users.put(updatedUser.getId(), updatedUser);
        return users.get(updatedUser.getId());
    }

    @Override
    public User getById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        users.remove(id);
    }

    private long generatedId() {
        return ++id;
    }

    private boolean checkUserWithEmail(long id, String email) {
        return users.values().stream()
                .filter(user -> user.getId() != id)
                .map(User::getEmail)
                .anyMatch(e -> e.equals(email));
    }
}
