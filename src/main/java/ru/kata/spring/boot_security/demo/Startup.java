package ru.kata.spring.boot_security.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.transaction.Transactional;
import java.util.Set;


@Component
public class Startup {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private RoleDao roleDao;

    public Startup(UserDao userDao, PasswordEncoder passwordEncoder, RoleDao roleDao) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleDao = roleDao;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void doSomethingAfterStartup() {
        System.out.println("hello world, I have just started up");

        if (!userDao.isExist("admin228@example.com")) {
            roleDao.saveRole(new Role("ROLE_ADMIN"));
            roleDao.saveRole(new Role("ROLE_USER"));
            Role role_admin = roleDao.findByName("ROLE_ADMIN");
            Role role_user = roleDao.findByName("ROLE_USER");
            User user = new User("admin228", "adminov", "admin228@example.com", passwordEncoder.encode("password"));
            User user2 = new User("user228", "userov", "user228@example.com", passwordEncoder.encode("password"));
            user.setRoles(Set.of(role_admin, role_user));
            user2.setRoles(Set.of(role_user));
            userDao.addUser(user);
            userDao.addUser(user2);
        }

    }
}