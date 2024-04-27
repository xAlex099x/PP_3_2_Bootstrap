package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.security.PersonDetails;
import ru.kata.spring.boot_security.demo.services.PeopleService;
import ru.kata.spring.boot_security.demo.services.RoleService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller

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

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person, Model model) {
        List<Role> roles = roleService.allRoles();
        model.addAttribute("roles", roles);
        return "admin/new_user";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("person") @Valid Person person,
                         @RequestParam(value = "roles", required = false) List<String> roles,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "admin/new_user";

        acceptRolesFromForm(person, roles);

        peopleService.addUser(person);
        return "redirect:/admin";
    }


//    @GetMapping("/edit/{id}")
//    public String edit(Model model, @PathVariable("id") long id) {
//        model.addAttribute("person", peopleService.userById(id));
//        List<Role> roles = roleService.allRoles();
//        model.addAttribute("roles", roles);
//        return "admin/edit_user_form";
//    }

    @PostMapping("/edit")
    public String edit(@RequestParam("user_ID") Long id,
                       @RequestParam("user_Username") String username,
                       @RequestParam("user_Password") String password,
                       @RequestParam("user_Email") String email,
                       @RequestParam("user_Role") String roles) {

        Person person = peopleService.userById(id);
        System.out.println(person);
        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setEmail(email);

        Set<Role> rolesSet = new HashSet<>();
        Role foundRole = roleService.findByName(roles);
        if (foundRole != null) {
            rolesSet.add(foundRole);
        } else {
            rolesSet.add(roleService.findByName("ROLE_USER"));
        }
        person.setRole(rolesSet);

        peopleService.updateUser(id, person);

        return "redirect:/admin";
    }

    private void acceptRolesFromForm(@ModelAttribute("person") @Valid Person person, @RequestParam(value = "roles", required = false) List<String> roles) {
        Set<Role> userRoles;
        if (roles != null) {
            userRoles = roles.stream()
                    .map(roleService::findByName)
                    .collect(Collectors.toSet());
            person.setRole(userRoles);
        } else {
            userRoles = Collections.singleton(roleService.findByName("ROLE_USER"));
            person.setRole(userRoles);
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        peopleService.deleteUser(id);
        return "redirect:/admin";
    }
}
