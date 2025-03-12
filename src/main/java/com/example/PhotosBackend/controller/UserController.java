package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Users;
import com.example.PhotosBackend.services.UserService;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/photos")
class UserController {
    @Autowired
    private UserService service;


    @GetMapping("/users")
    public List<Users> getUsers() {
        return service.getUsers();
    }

    @PostMapping("/user")
    public ResponseEntity<Object> addUser(@Valid @RequestBody Users users){

        try {
            System.out.println(users);
            Users newuser = service.addUser(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(newuser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}