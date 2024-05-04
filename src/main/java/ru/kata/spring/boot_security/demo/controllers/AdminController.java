package ru.kata.spring.boot_security.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.configs.RolesEnum;
import ru.kata.spring.boot_security.demo.dto.PersonDTO;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.security.PersonDetails;
import ru.kata.spring.boot_security.demo.services.PeopleServiceImpl;
import ru.kata.spring.boot_security.demo.util.EntityNotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@Validated
@RequestMapping("/admin")
public class AdminController {
    private final PeopleServiceImpl peopleServiceImpl;

    @Autowired
    public AdminController(PeopleServiceImpl peopleServiceImpl) {
        this.peopleServiceImpl = peopleServiceImpl;
    }

    @GetMapping()
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails details = (PersonDetails) auth.getPrincipal();
        model.addAttribute("person", this.convertToDto(details.getPerson()));
        model.addAttribute("people", this.convertToDtoList(peopleServiceImpl.allPeople()));
        return "admin/admin_page";
    }

    @PostMapping("/new")
    public String create(@RequestParam("newUser_Username") @NotBlank @Size(min = 2, max = 40) String username,
                         @RequestParam("newUser_Password") @NotBlank @Size(min = 2, max = 16) String password,
                         @RequestParam("newUser_Email") @Email String email,
                         @RequestParam(value = "newUser_Role", required = false) String[] roles) {
        try {
            peopleServiceImpl.userByUsername(username);
            throw new RuntimeException("User with this username already exists");
        } catch (EntityNotFoundException e) {
            peopleServiceImpl.addUser(username, password, email, roles);
        }
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam("user_ID") Long id,
                       @RequestParam("user_Username") @NotBlank @Size(min = 2, max = 40) String username,
                       @RequestParam("user_Password") @NotBlank @Size(min = 2, max = 16) String password,
                       @RequestParam("user_Email") @Email String email,
                       @RequestParam(value = "user_Role", required = false) String[] roles) {

        peopleServiceImpl.updateUser(id, username, password, email, roles);

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("deleteUser_ID") Long id) {
        peopleServiceImpl.deleteUser(id);
        return "redirect:/admin";
    }

    private PersonDTO convertToDto(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);

        Set<String> rolesWithoutPrefix = new HashSet<>();
        for (Role role : person.getRoles()) {
            Optional<String> roleOptional = RolesEnum.getRoleNameWithoutPrefix(role.getName());
            roleOptional.ifPresent(rolesWithoutPrefix::add);
        }
        personDTO.setRoles(rolesWithoutPrefix);
        return personDTO;
    }

    private List<PersonDTO> convertToDtoList(List<Person> people) {
        ModelMapper modelMapper = new ModelMapper();
        List<PersonDTO> resultList = new ArrayList<>();
        for (Person person : people) {
            resultList.add(this.convertToDto(person));
        }
        return resultList;
    }
}
