package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userDao.getUserById(id);
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        userDao.addUser(user);
    }


    @Override
    @Transactional
    public void addUser(User user, List<Long> roleIds) {
        Set<Role> roles = roleIds.stream()
                .map(id -> roleDao.getRoleById(id)) // Предполагается, что у тебя есть такой метод в RoleDao
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userDao.addUser(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, User updatedUser) {
        User existingUser = userDao.getUserById(id);
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            userDao.updateUser(existingUser);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        User user = userDao.findByEmail(email);
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Override
    @Transactional
    public void initializeRoles(User user) {
        Hibernate.initialize(user.getRoles());
    }
}