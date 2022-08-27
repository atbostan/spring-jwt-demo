package com.bossware.jwtdemoapp.core.exception.domain.user;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
