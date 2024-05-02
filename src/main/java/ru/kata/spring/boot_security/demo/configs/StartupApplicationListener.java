package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.services.PeopleServiceImpl;
import ru.kata.spring.boot_security.demo.services.RoleServiceImpl;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleServiceImpl roleServiceImpl;
    private final PeopleServiceImpl peopleServiceImpl;


    @Autowired
    public StartupApplicationListener(RoleServiceImpl roleServiceImpl, PeopleServiceImpl peopleServiceImpl) {
        this.roleServiceImpl = roleServiceImpl;
        this.peopleServiceImpl = peopleServiceImpl;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Проверяем наличие ролей Admin и User в базе данных, а так же root пользователя.
        Role adminRole = roleServiceImpl.findByName("ROLE_ADMIN");
        Role userRole = roleServiceImpl.findByName("ROLE_USER");
        Person Admin = peopleServiceImpl.userByUsername("Testuser");

        if (adminRole == null) {
            roleServiceImpl.save(new Role("ROLE_ADMIN"));
        }
        if (userRole == null) {
            roleServiceImpl.save(new Role("ROLE_USER"));
        }
        if (Admin == null) {
            peopleServiceImpl.addUser("Testuser","Testuser","Test@Test.com","ROLE_ADMIN");
        }
    }
}
