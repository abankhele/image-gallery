package com.example.PhotosBackend.services;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.repository.PhotoRepository;
import com.example.PhotosBackend.services.GCSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    private VisionService visionService;

    private final PhotoRepository photoRepository;
    private final GCSService storageService;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, GCSService storageService) {
        this.photoRepository = photoRepository;
        this.storageService = storageService;
    }

    public List<Photo> getAllPhotosByUserId(String userId) {
        return photoRepository.findByUserId(userId);
    }

    public Photo getPhotoById(String photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with id: " + photoId));
    }

    public List<Photo> getPhotosByAlbumId(String albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    public List<Photo> getPhotosByUserIdAndTag(String userId, String tag) {
        return photoRepository.findByUserIdAndTagsContaining(userId, tag);
    }

    public Photo uploadPhoto(MultipartFile file, String userId, String albumId, List<String> tags) throws IOException {
        // Upload file to Google Cloud Storage
        String gcsUrl = storageService.uploadFile(file, userId);

        List<String> detectedLabels = visionService.detectLabels(file.getBytes());

        // Combine user-provided tags with detected labels
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.addAll(detectedLabels);

        // Create and save photo metadata
        Photo photo = new Photo();
        photo.setUserId(userId);
        photo.setAlbumId(albumId);
        photo.setGcsUrl(gcsUrl);
        photo.setTags(tags);
        photo.setSize(file.getSize());
        photo.setFormat(file.getContentType());
        photo.setUploadedOn(new Date());

        return photoRepository.save(photo);
    }

    public void deletePhoto(String photoId) {
        Photo photo = getPhotoById(photoId);

        // Delete from Google Cloud Storage
        storageService.deleteFile(photo.getGcsUrl());

        // Delete from MongoDB
        photoRepository.deleteById(photoId);
    }
    public List<Photo> getAllPhotosByUserIdWithDisplayUrls(String userId, String baseUrl) {
        List<Photo> photos = photoRepository.findByUserId(userId);
        photos.forEach(photo -> {
            photo.setDisplayUrl(baseUrl + "/api/photos/image/" + photo.getId());
        });
        return photos;
    }

    public List<Photo> searchPhotosByUserIdAndTags(String userId, String query) {
        return photoRepository.findByUserIdAndTagsContainingIgnoreCase(userId, query);
    }

    public List<Photo> searchPhotosByTags(String query) {
        return photoRepository.findByTagsContainingIgnoreCase(query);
    }

}
