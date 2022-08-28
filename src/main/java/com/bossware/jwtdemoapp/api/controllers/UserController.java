package com.bossware.jwtdemoapp.api.controllers;

import com.bossware.jwtdemoapp.application.services.UserService;
import com.bossware.jwtdemoapp.core.constants.SecurityConstants;
import com.bossware.jwtdemoapp.core.exception.ExceptionHandlerHelper;
import com.bossware.jwtdemoapp.core.exception.domain.email.EmailExistException;
import com.bossware.jwtdemoapp.core.exception.domain.user.UserNameExistException;
import com.bossware.jwtdemoapp.core.models.entities.User;
import com.bossware.jwtdemoapp.core.security.UserPrincipal;
import com.bossware.jwtdemoapp.core.utilities.JWTProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandlerHelper {


    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTProvider jwtProvider;

   @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)  throws UserNameExistException, EmailExistException, UsernameNotFoundException {
       User registeredUser =
               userService.register(user.getFirstName(),user.getLastName(),user.getUserName(),user.getPassword());
       return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user)   {
        authenticate(user.getUserName(),user.getPassword());
        User loggedUser = userService.findUserByUsername(user.getUserName());
        UserPrincipal principal = new UserPrincipal(loggedUser);
        HttpHeaders jwtHeader = getJwtHeader(principal);
        return new ResponseEntity<>(loggedUser,jwtHeader, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal principal) {

       HttpHeaders headers = new HttpHeaders();
       headers.add(SecurityConstants.JWT_TOKEN_HEADER,jwtProvider.generateJWTToken(principal));
       return  headers;
    }

    private void authenticate(String userName, String password) {
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName,password));
    }


}
