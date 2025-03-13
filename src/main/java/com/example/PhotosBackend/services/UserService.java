package com.example.PhotosBackend.services;


import com.example.PhotosBackend.model.Users;
import com.example.PhotosBackend.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UsersRepo repo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public List<Users> getUsers() {
        return repo.findAll();
    }

    public Users addUser(Users user){

        if (user.getName().isBlank() || user.getPasswordHash().isBlank()) {
            throw new RuntimeException("Name and Password cannot be empty.");
        }
        try {
            user.setPasswordHash(encoder.encode(user.getPasswordHash()));
            return repo.save(user);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Email already exists.");
        }catch (Exception e){
            throw new RuntimeException("An error occurred while adding the user.");
        }
    }
}
