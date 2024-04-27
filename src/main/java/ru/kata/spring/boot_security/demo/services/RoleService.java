package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> allRoles() {
        return roleRepository.findAll();
    }

    public Role findByName(String name) {
        Optional<Role> result = roleRepository.findByName(name);
        return result.orElse(null);
    }

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }

}
