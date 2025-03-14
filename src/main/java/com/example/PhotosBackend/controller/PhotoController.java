package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.services.GCSService;
import com.example.PhotosBackend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {
    @Autowired
    private GCSService gcsService;

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("albumId") String albumId,
            @RequestParam(value = "tags", required = false) String tags
    ) {
        try {

            String gcsUrl = gcsService.uploadFile(file);

            long size1 = file.getSize();  // Get file size in bytes
            String format1 = file.getContentType();

            // Create a new photo object with the metadata and GCS URL
            Photo photo = new Photo();
            photo.setUserId(userId);
            photo.setAlbumId(albumId);
            photo.setGcsUrl(gcsUrl);
            photo.setTags(List.of(tags.split(",")));   // Convert comma-separated tags to a list
            photo.setSize(size1);
            photo.setFormat(format1);

            // Save the photo metadata to MongoDB
            photo = photoService.savePhoto(photo);

            return ResponseEntity.ok(photo);  // Return the photo object as the response
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

}




