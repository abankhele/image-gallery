package com.example.PhotosBackend.controller;

import com.example.PhotosBackend.model.Photo;
import com.example.PhotosBackend.services.GCSService;
import com.example.PhotosBackend.services.PhotoService;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/photo")
public class PhotoController {
    @Autowired
    private GCSService gcsService;

    @Autowired
    private PhotoService photoService;

    public PhotoController() throws IOException {
    }

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

    @PostMapping("/batch-upload")
    public ResponseEntity<?> batchUploadPhotos(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId,
            @RequestParam("albumId") String albumId,
            @RequestParam(value = "tags", required = false) String tags
    ) {
        List<String> tagList = tags != null ? List.of(tags.split(",")) : List.of();

        List<CompletableFuture<Photo>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return gcsService.uploadFiles(file, userId, albumId, tagList);
                    } catch (Exception e) {
                        throw new RuntimeException("Upload failed: " + e.getMessage());
                    }
                }))
                .collect(Collectors.toList());

        List<Photo> uploadedPhotos = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(photoService.saveAllPhotos(uploadedPhotos));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photo>> getUserPhotos(@PathVariable String userId){
        List<Photo> photos = photoService.getPhotosByUserId(userId);

        if(photos.isEmpty()){
                return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(photos);
        }



    }

    Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/thinking-pagoda-453620-u3-e787b1d5f7f9.json"));

    private final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();


    @Value("${gcp.storage.bucket}")
    private String bucketName;


    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws IOException {
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(blob.getContent());
    }




}




