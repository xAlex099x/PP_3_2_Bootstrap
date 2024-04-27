package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.services.PeopleService;
import ru.kata.spring.boot_security.demo.services.RoleService;

import java.util.Collections;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleService roleService;
    private final PeopleService peopleService;


    @Autowired
    public StartupApplicationListener(RoleService roleService, PeopleService peopleService) {
        this.roleService = roleService;
        this.peopleService = peopleService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Проверяем наличие ролей Admin и User в базе данных, а так же root пользователя.
        Role adminRole = roleService.findByName("ROLE_ADMIN");
        Role userRole = roleService.findByName("ROLE_USER");
        Person Admin = peopleService.userByUsername("Testuser");

        if (adminRole == null) {
            roleService.save(new Role("ROLE_ADMIN"));
        }
        if (userRole == null) {
            roleService.save(new Role("ROLE_USER"));
        }
        if (Admin == null) {
            Person person = new Person(
                    "Testuser",
                    "Testuser",
                    "Test@Test.com",
                    Collections.singleton(roleService.findByName("ROLE_ADMIN")));

            peopleService.addUser(person);
        }
    }
}
