package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.security.PersonDetails;
import ru.kata.spring.boot_security.demo.services.PeopleService;
import ru.kata.spring.boot_security.demo.services.RoleService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping("/admin")
public class AdminController {
    private final RoleService roleService;
    private final PeopleService peopleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(PeopleService peopleService,
                           RoleService roleService, PasswordEncoder passwordEncoder) {
        this.peopleService = peopleService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails details = (PersonDetails) auth.getPrincipal();
        model.addAttribute("person", details.getPerson());
        model.addAttribute("people", peopleService.allPeople());
        return "admin/admin_page";
    }

    @PostMapping("/new")
    public String create(@RequestParam("newUser_Username") String username,
                         @RequestParam("newUser_Password") String password,
                         @RequestParam("newUser_Email") String email,
                         @RequestParam(value = "newUser_Role", required = false) String roles) {

        //Если пользователь с таким именем уже существует, то возвращаемся на страницу.
        //Отображение ошибок сделаю на следующем задании.
        if (peopleService.userByUsername(username)!= null) {
            return "redirect:/admin?error=true";
        }

        Person person = new Person();
        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setEmail(email);

        Set<Role> rolesSet = getRoles(roles);
        person.setRole(rolesSet);

        peopleService.addUser(person);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam("user_ID") Long id,
                       @RequestParam("user_Username") String username,
                       @RequestParam("user_Password") String password,
                       @RequestParam("user_Email") String email,
                       @RequestParam(value = "user_Role", required = false) String roles) {


        Person person = peopleService.userById(id);
        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setEmail(email);

        Set<Role> rolesSet = getRoles(roles);
        person.setRole(rolesSet);

        peopleService.updateUser(id, person);

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("deleteUser_ID") Long id) {
        peopleService.deleteUser(id);
        return "redirect:/admin";
    }

    private Set<Role> getRoles(String roles) {
        Set<Role> rolesSet = new HashSet<>();
        Role foundRole = roleService.findByName(roles);
        if (foundRole != null) {
            rolesSet.add(foundRole);
        } else {
            rolesSet.add(roleService.findByName("ROLE_USER"));
        }
        return rolesSet;
    }
}
