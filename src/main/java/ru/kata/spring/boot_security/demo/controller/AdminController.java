package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin") // Изменили путь для доступа к контроллеру на /admin
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Set<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        if (principal != null) {
            model.addAttribute("principal",(UserDetailsImpl) principal);
        }
        return "user-list";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", allRoles);
        System.out.println("Loaded roles: " + allRoles); // Для отладки
        if (principal != null) {
            model.addAttribute("principal", (UserDetailsImpl)principal);
        }
        return "user-list";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("newUser") User user,
                          @RequestParam(value = "roles", required = false) List<Long> roles) {
        if (roles != null) {
            userService.addUser(user, roles);
        } else {
            userService.addUser(user, new ArrayList<>()); // Обработка случая без ролей
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditUserForm(@RequestParam("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        User user = userService.getUserById(userId);
        userService.initializeRoles(user);
        model.addAttribute("user", user);
        if (principal != null) {
            model.addAttribute("principal",(UserDetailsImpl) principal);
        }
        return "user-list";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User updatedUser) {
        userService.updateUser(updatedUser.getId(), updatedUser);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }
}