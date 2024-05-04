package ru.kata.spring.boot_security.demo.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.configs.RolesEnum;
import ru.kata.spring.boot_security.demo.dto.PersonDTO;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.services.RoleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DTOConverter {

    private static RoleService roleServiceImpl;
    private static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public DTOConverter(RoleService roleServiceImpl) {
        this.roleServiceImpl = roleServiceImpl;
    }

    public PersonDTO convertToDto(Person person) {
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

    public List<PersonDTO> convertToDtoList(List<Person> people) {
        ModelMapper modelMapper = new ModelMapper();
        List<PersonDTO> resultList = new ArrayList<>();
        for (Person person : people) {
            resultList.add(this.convertToDto(person));
        }
        return resultList;
    }
}
