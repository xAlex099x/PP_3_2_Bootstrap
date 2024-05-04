package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.configs.RolesEnum;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.PeopleRepository;
import ru.kata.spring.boot_security.demo.util.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class PeopleServiceImpl implements PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleServiceImpl;

    @Autowired
    public PeopleServiceImpl(PeopleRepository peopleRepository,
                             PasswordEncoder passwordEncoder, RoleServiceImpl roleServiceImpl) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleServiceImpl = roleServiceImpl;
    }

    public List<Person> allPeople() {
        return peopleRepository.findAll();
    }

    public Person userByUsername(String username) {
        Optional<Person> result = peopleRepository.findByUsername(username);
        return result.orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public Person userById(long id) {
        Optional<Person> result = peopleRepository.findById(id);
        return result.orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void addUser(String username, String password, String email, String[] roles) {

        Person person = new Person();

        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setEmail(email);
        Set<Role> rolesSet = getRoles(roles);
        person.setRoles(rolesSet);

        peopleRepository.save(person);
    }

    @Transactional
    public void updateUser(long id, String username, String password, String email, String[] roles) {
        if (this.userById(id) != null) {
            Person updatedUser = this.userById(id);

            updatedUser.setUsername(username);
            updatedUser.setPassword(passwordEncoder.encode(password));
            updatedUser.setEmail(email);
            Set<Role> rolesSet = getRoles(roles);
            updatedUser.setRoles(rolesSet);

            peopleRepository.save(updatedUser);
        } else {
            throw new EntityNotFoundException("User with id not found");
        }

    }

    @Transactional
    public void deleteUser(long id) {
        if (this.userById(id) != null) {
            peopleRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User with id not found");
        }
    }

    private Set<Role> getRoles(String[] roles) {
        Set<Role> rolesSet = new HashSet<>();
        Role foundRole = roleServiceImpl.findByName(roles[0]);
        if (foundRole != null) {
            for (String role : roles) {
                rolesSet.add(roleServiceImpl.findByName(role));
            }
        } else {
            rolesSet.add(roleServiceImpl.findByName(RolesEnum.USER.getRoleName()));
        }
        return rolesSet;
    }
}
