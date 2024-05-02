package ru.kata.spring.boot_security.demo.util;

import java.util.function.Supplier;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
