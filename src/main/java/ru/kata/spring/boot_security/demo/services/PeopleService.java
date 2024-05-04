package ru.kata.spring.boot_security.demo.services;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.util.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PeopleService {

    public List<Person> allPeople();

    public Person userByUsername(String username);

    public Person userById(long id);

    public void addUser(String username, String password, String email, String[] roles);

    public void updateUser(long id, String username, String password, String email, String[] roles);

    public void deleteUser(long id);
}
