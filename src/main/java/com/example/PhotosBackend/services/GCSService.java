package com.example.PhotosBackend.services;

import com.example.PhotosBackend.model.Photo;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class GCSService {

    Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/thinking-pagoda-453620-u3-e787b1d5f7f9.json"));

    private final Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();


    @Value("${gcp.storage.bucket}")
    private String bucketName;

    public GCSService() throws IOException {
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        return blob.getMediaLink();
    }

    public Photo uploadFiles(MultipartFile file, String userId, String albumId, List<String> tags) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        Bucket bucket = storage.get(bucketName);
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        // Enable Resumable Uploads for Large Files
        storage.create(blobInfo, file.getInputStream());

        // Creating a Photo Object with Metadata
        Photo photo = new Photo();
        photo.setUserId(userId);
        photo.setAlbumId(albumId);
        photo.setGcsUrl(String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName));
        photo.setTags(tags);
        photo.setSize(file.getSize());
        photo.setFormat(file.getContentType());
        photo.setUploadedOn(new Date());

        return photo;
    }

}
