package com.bossware.jwtdemoapp.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

   @GetMapping("/home")
    public String showUser(){
       return "WORKS";
    }
}
