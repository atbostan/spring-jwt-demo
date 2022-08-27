package com.bossware.jwtdemoapp.core.exception.domain.user;

public class UserNameExistException extends  RuntimeException{
    public UserNameExistException(String message) {
        super(message);
    }
}
