package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User update(User user);

    List<User> getAll();

    User getById(long id);

    void delete(long id);
}
