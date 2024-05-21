package ru.kata.spring.boot_security.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.transaction.Transactional;
import java.util.Set;


@Component
public class Startup {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    public Startup(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void doSomethingAfterStartup() {
        System.out.println("hello world, I have just started up");

        if (!userDao.isExist("admin228@example.com")) {
            User user = new User("admin228", "adminov", "admin228@example.com", passwordEncoder.encode("password"));
            User user2 = new User("user228", "userov", "user228@example.com", passwordEncoder.encode("password"));
            Role role_admin = new Role("ROLE_ADMIN");
            Role role_user = new Role("ROLE_USER");
            user.setRoles(Set.of(role_admin, role_user));
            role_admin.setUsers(Set.of(user));
            role_user.setUsers(Set.of(user,user2));
            user2.setRoles(Set.of(role_user));
            userDao.addUser(user);
            userDao.addUser(user2);
        }

    }
}
