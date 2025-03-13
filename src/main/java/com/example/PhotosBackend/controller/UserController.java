package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Users;
import com.example.PhotosBackend.services.JwtService;
import com.example.PhotosBackend.services.UserService;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtservice;
    @Autowired
    private UserService service;


    @GetMapping("/users")
    public List<Users> getUsers() {
        return service.getUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<Object> addUser(@RequestBody Users users){

        try {
            System.out.println(users);
            Users newuser = service.addUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(newuser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPasswordHash()));
        if(authentication.isAuthenticated()){
            return jwtservice.generateToken(user.getEmail());
        }
        else{
            return "Failure";
        }
    }

}