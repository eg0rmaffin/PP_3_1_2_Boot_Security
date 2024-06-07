package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ApiRestController {

    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public ApiRestController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<Set<User>> getAllUsers() {
        Set<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            System.out.println("Received user data for update: " + userDetails); // Логирование полученных данных

            User existingUser = userService.getUserById(id);
            if (existingUser != null) {
                existingUser.setFirstName(userDetails.getFirstName());
                existingUser.setLastName(userDetails.getLastName());
                existingUser.setEmail(userDetails.getEmail());

                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }

                Set<Role> roles = userDetails.getRoles().stream()
                        .map(role -> roleService.findByName(role.getName()))
                        .collect(Collectors.toSet());


                if (roles.isEmpty()) {
                    System.out.println("Roles not found");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }

                existingUser.setRoles(roles);
                userService.updateUser(id, existingUser);
                return ResponseEntity.ok(existingUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User existingUser = userService.getUserById(id);
        if (existingUser != null) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> details = new HashMap<>();
        details.put("username", userDetails.getUsername());
        details.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(details);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<String>> getAllRoles() {
        Set<String> roles = roleService.getAllRolesString();
        return ResponseEntity.ok(roles);
    }


    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername()); // метод должен быть реализован в UserService для поиска пользователя по email
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            System.out.println("Received user data: " + user); // Логирование полученных данных

            // Обработка ролей
            Set<Role> roles = user.getRoles().stream()
                    .map(role -> {
                        Role foundRole = roleService.findByName(role.getName());
                        if (foundRole == null) {
                            throw new RuntimeException("Role not found: " + role.getName());
                        }
                        return foundRole;
                    })
                    .collect(Collectors.toSet());

            if (roles.isEmpty()) {
                System.out.println("Roles not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("/currentUserDetails")
    public ResponseEntity<Map<String, Object>> getCurrentUserDetails(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());

        if (currentUser != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", currentUser.getId());
            userMap.put("firstName", currentUser.getFirstName());
            userMap.put("lastName", currentUser.getLastName());
            userMap.put("email", currentUser.getEmail());
            userMap.put("roles", currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return ResponseEntity.ok(userMap);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }





}