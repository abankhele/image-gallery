package com.example.PhotosBackend.repository;

import com.example.PhotosBackend.model.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {


    List<Photo> findByUserId(String userId);
    List<Photo> findByAlbumId(String albumId);
    List<Photo> findByUserIdAndTagsContaining(String userId, String tag);

    List<Photo> findByUserIdAndTagsContainingIgnoreCase(String userId, String tag);
    List<Photo> findByTagsContainingIgnoreCase(String tag);
}
