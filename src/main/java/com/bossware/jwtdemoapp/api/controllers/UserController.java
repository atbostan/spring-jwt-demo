package com.bossware.jwtdemoapp.api.controllers;

import com.bossware.jwtdemoapp.application.services.UserService;
import com.bossware.jwtdemoapp.core.exception.ExceptionHandlerHelper;
import com.bossware.jwtdemoapp.core.exception.domain.email.EmailExistException;
import com.bossware.jwtdemoapp.core.exception.domain.user.UserNameExistException;
import com.bossware.jwtdemoapp.core.models.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandlerHelper {

    @Autowired
    UserService userService;

   @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)  throws UserNameExistException, EmailExistException, UsernameNotFoundException {
       User registeredUser =
               userService.register(user.getFirstName(),user.getLastName(),user.getUserName(),user.getPassword());
       return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }


}
