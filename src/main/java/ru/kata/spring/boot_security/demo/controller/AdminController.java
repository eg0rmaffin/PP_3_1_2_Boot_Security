package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @Transactional
    public String listUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        List<User> users = userService.getAllUsers();
        users.forEach(userService::initializeRoles);
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);

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
    @Transactional
    public String showEditUserForm(@RequestParam("id") Long userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        User user = userService.getUserById(userId);
        userService.initializeRoles(user);
        model.addAttribute("user", user);
        if (principal != null) {
            model.addAttribute("principal",(UserDetailsImpl) principal);
        }
        return "edit-user";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User updatedUser, @RequestParam List<Long> roles) {

        if(roles!=null){
            Set<Role> roleObjects = roles.stream()
                    .map(roleId -> roleService.getRoleById(roleId))
                    .collect(Collectors.toSet());

            updatedUser.setRoles(roleObjects);
        }
        userService.updateUser(updatedUser.getId(), updatedUser);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }
}