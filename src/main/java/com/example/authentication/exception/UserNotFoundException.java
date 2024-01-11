package com.example.authentication.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User with the entered credentials is not found!");
    }

}
