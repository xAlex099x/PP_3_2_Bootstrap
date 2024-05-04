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
        Role adminRole = roleServiceImpl.findByName(RolesEnum.ADMIN.getRoleName());
        Role userRole = roleServiceImpl.findByName(RolesEnum.USER.getRoleName());
        Person Admin = peopleServiceImpl.userByUsername("Testuser");

        if (adminRole == null) {
            roleServiceImpl.save(new Role(RolesEnum.ADMIN.getRoleName()));
        }
        if (userRole == null) {
            roleServiceImpl.save(new Role(RolesEnum.USER.getRoleName()));
        }
        if (Admin == null) {
            peopleServiceImpl.addUser("Testuser","Testuser","Test@Test.com", new String[]{RolesEnum.ADMIN.getRoleName()});
        }
    }
}
