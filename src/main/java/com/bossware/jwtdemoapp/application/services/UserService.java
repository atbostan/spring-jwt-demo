package com.bossware.jwtdemoapp.application.services;

import com.bossware.jwtdemoapp.core.models.entities.User;

import java.util.List;

public interface UserService {
    User register(String firstName,String lastName, String userName,String email);
    List<User> getUsers();
    User findUserByUsername(String userName);
    User findUserByEmail(String email);
 }
