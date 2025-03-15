package com.example.PhotosBackend.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
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


}
