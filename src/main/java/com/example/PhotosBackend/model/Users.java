package com.example.PhotosBackend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Indexed(unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String passwordHash;

    private String role="USER";
}
