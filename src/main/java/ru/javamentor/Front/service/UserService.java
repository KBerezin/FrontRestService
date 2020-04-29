package ru.javamentor.Front.service;

import ru.javamentor.Front.entity.User;
import ru.javamentor.Front.exception.UserAlreadyExistsException;

import java.util.List;

public interface UserService {

    void save(User user) throws UserAlreadyExistsException;

    void update(User user);

    List<User> listAll();

    User get(Long id);

    void delete(Long id);
}
