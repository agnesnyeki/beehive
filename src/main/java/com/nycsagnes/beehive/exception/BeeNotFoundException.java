package com.nycsagnes.beehive.exception;

public class BeeNotFoundException extends RuntimeException {
    public BeeNotFoundException(Long id) {
        super("Bee not found with id: " + id);
    }
}
