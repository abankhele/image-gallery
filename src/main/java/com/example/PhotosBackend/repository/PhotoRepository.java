package com.example.PhotosBackend.repository;

import com.example.PhotosBackend.model.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {


    List<Photo> findByUserId(String userId);
}
