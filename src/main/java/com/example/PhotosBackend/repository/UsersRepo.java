package com.example.PhotosBackend.repository;

import com.example.PhotosBackend.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends MongoRepository<Users, String> {
}
