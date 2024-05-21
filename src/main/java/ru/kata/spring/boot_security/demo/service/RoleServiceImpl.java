package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        List<Role> roles = roleDao.getAllRoles();
        System.out.println("Roles loaded: "+ roles);
        return roles;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleDao.getRoleById(id);
    }

    @Override
    @Transactional
    public void saveRole(Role role) {
        roleDao.saveRole(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleDao.deleteRole(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleDao.findByName(name);
    }
}
