package ru.kata.spring.boot_security.demo.configs;

import java.util.Optional;

public enum RolesEnum {
    ADMIN( "ROLE_ADMIN","ADMIN" ),
    USER( "ROLE_USER","USER" );

    private final String roleName;
    private final String roleNameWithoutPrefix;

    RolesEnum(String roleName,String roleNameWithoutPrefix) {
        this.roleName = roleName;
        this.roleNameWithoutPrefix = roleNameWithoutPrefix;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleNameWithoutPrefix() {
        return roleNameWithoutPrefix;
    }

    public static Optional<String> getRoleNameWithoutPrefix(String roleName) {
        for (RolesEnum role : RolesEnum.values()) {
            if (role.getRoleName().equals(roleName)) {
                return Optional.of(role.getRoleNameWithoutPrefix());
            }
        }
        return Optional.empty();
    }
}