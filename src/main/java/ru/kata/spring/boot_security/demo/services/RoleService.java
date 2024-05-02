package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;

public interface RoleService {

    public List<Role> allRoles();

    public Role findByName(String name);

    public void save(Role role);
}
