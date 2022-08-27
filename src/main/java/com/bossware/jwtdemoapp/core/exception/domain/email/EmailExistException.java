package com.bossware.jwtdemoapp.core.exception.domain.email;

public class EmailExistException extends  RuntimeException{
    public EmailExistException(String message) {
        super(message);
    }
}
