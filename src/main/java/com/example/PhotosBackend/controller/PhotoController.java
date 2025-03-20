package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "http://localhost:5173")
public class PhotoController {

    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photo>> getAllUserPhotos(@PathVariable String userId) {
        List<Photo> photos = photoService.getAllPhotosByUserId(userId);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable String photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        return ResponseEntity.ok(photo);
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
}
