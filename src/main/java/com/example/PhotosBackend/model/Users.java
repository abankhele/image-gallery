package com.example.PhotosBackend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Component;



@Document(collection = "users-db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Users {
    @MongoId
    private String id;


    private String name;

    @Indexed(unique = true)
    private String email;


    private String passwordHash;

    private String role="USER";
}
