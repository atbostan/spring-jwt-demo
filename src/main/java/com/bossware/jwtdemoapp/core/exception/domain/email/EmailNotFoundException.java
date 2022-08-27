package com.bossware.jwtdemoapp.core.exception.domain.email;

public class EmailNotFoundException extends  RuntimeException{
    public EmailNotFoundException(String message) {
        super(message);
    }
}
