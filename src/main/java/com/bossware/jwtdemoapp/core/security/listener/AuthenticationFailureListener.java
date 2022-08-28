package com.bossware.jwtdemoapp.core.security.listener;

import com.bossware.jwtdemoapp.application.services.impl.LoggingAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationFailureListener {

    @Autowired
    private LoggingAttemptService loggingAttemptService;

    @EventListener
    public  void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
        Object principal =event.getAuthentication().getPrincipal();
        if(principal instanceof  String){
            String userName= (String) principal;
            loggingAttemptService.addUserToLoggingAttemptCache(userName);
        }
    }
}

