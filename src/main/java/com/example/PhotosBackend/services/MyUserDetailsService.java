package com.example.PhotosBackend.services;

import com.example.PhotosBackend.model.Users;
import com.example.PhotosBackend.repository.UserPrincipal;
import com.example.PhotosBackend.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService{
    @Autowired
    private UsersRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user =repo.findByEmail(username);
        if(user==null){
            System.out.println("email 404");
            throw new UsernameNotFoundException("email 404");
        }
        return new UserPrincipal(user);
    }
}
