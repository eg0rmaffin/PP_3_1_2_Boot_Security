package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    void saveRole(Role role);
    void deleteRole(Long id);
    Role findByName(String name);

    Set<String> getAllRolesString();
}