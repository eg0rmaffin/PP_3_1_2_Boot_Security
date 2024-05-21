package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    Set<User> getAllUsers();
    User getUserById(Long id);
    void addUser(User user);
    void addUser(User user, List<Long> roleIds);
    void updateUser(Long id, User updatedUser);
    void deleteUser(Long id);

    User findByEmail(String email);
    void initializeRoles(User user);
}
