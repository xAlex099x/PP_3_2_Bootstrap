package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.security.PersonDetails;

@Controller
public class UserController {

    @GetMapping("/index")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails details = (PersonDetails) auth.getPrincipal();
        model.addAttribute("person", details.getPerson());
        return "/index";
    }
}
