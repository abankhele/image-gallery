package com.example.PhotosBackend.services;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    public Object saveAllPhotos(List<Photo> uploadedPhotos) {
        return photoRepository.saveAll(uploadedPhotos);
    }

    public List<Photo> getPhotosByUserId(String userId) {
        return photoRepository.findByUserId(userId);
    }
}
