package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class  UserRepositoryImpl implements UserRepository {
    private long id = 0;
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(generatedId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        checkUser(id);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getById(long id) {
        checkUser(id);
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        checkUser(id);
        users.remove(id);
    }

    private void checkUser(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    private long generatedId() {
        return ++id;
    }
}
