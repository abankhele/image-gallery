package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.services.GCSService;
import com.example.PhotosBackend.services.PhotoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "http://localhost:5173")
public class PhotoController {

    @Autowired
    private final PhotoService photoService;
    @Autowired
    private GCSService storageservice;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photo>> getAllUserPhotos(
            @PathVariable String userId,
            HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        List<Photo> photos = photoService.getAllPhotosByUserIdWithDisplayUrls(userId, baseUrl);
        return ResponseEntity.ok(photos);
    }


    @GetMapping("/{photoId}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable String photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        return ResponseEntity.ok(photo);
    }
    @GetMapping("/image/{photoId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String photoId) {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            byte[] imageBytes = storageservice.downloadFile(photo.getGcsUrl());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(photo.getFormat()))
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Photo>> getPhotosByAlbumId(@PathVariable String albumId) {
        List<Photo> photos = photoService.getPhotosByAlbumId(albumId);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/user/{userId}/tag/{tag}")
    public ResponseEntity<List<Photo>> getPhotosByUserIdAndTag(
            @PathVariable String userId,
            @PathVariable String tag) {
        List<Photo> photos = photoService.getPhotosByUserIdAndTag(userId, tag);
        return ResponseEntity.ok(photos);
    }

    @PostMapping
    public ResponseEntity<Photo> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam(value = "albumId", required = false) String albumId,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        try {
            Photo photo = photoService.uploadPhoto(file, userId, albumId, tags);
            return ResponseEntity.ok(photo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable String photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Photo>> searchPhotos(
            @RequestParam String query,
            @RequestParam(required = false) String userId) {
        List<Photo> photos;
        if (userId != null) {
            photos = photoService.searchPhotosByUserIdAndTags(userId, query);
        } else {
            photos = photoService.searchPhotosByTags(query);
        }
        return ResponseEntity.ok(photos);
    }
}
