package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository,
                         PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> allPeople() {
        return peopleRepository.findAll();
    }

    public Person userByUsername(String username) {
        Optional<Person> result = peopleRepository.findByUsername(username);
        return result.orElse(null);
    }

    public Person userById(long id) {
        Optional<Person> result = peopleRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public void addUser(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleRepository.save(person);
    }

    @Transactional
    public void updateUser(long id, Person updatedUser) {
        updatedUser.setId(id);
        peopleRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(long id) {
        peopleRepository.deleteById(id);
    }
}
