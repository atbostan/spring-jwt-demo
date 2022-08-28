package com.bossware.jwtdemoapp.core.security.listener;

import com.bossware.jwtdemoapp.application.services.impl.LoggingAttemptService;
import com.bossware.jwtdemoapp.core.models.entities.User;
import com.bossware.jwtdemoapp.core.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    @Autowired
    private LoggingAttemptService loggingAttemptService;

    @EventListener
    public  void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            UserPrincipal user = (UserPrincipal)principal;
            loggingAttemptService.evictUserFromLoggingAttemptCache(user.getUsername());
        }
    }
}
