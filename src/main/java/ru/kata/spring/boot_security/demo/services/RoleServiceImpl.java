package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.util.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> allRoles() {
        return roleRepository.findAll();
    }

    public Role findByName(String name) {
        Optional<Role> result = roleRepository.findByName(name);
        return result.orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }

}
