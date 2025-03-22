package com.example.PhotosBackend.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class GCSService {

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    private Storage storage;

    @PostConstruct
    public void initialize() throws IOException {
        // Load credentials from the JSON key file
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream("src/main/resources/thinking-pagoda-453620-u3-e787b1d5f7f9.json"));

        // Initialize storage with credentials
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadFile(MultipartFile file, String userId) throws IOException {
        // Generate a unique filename
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "-" + originalFilename;

        // Create blob info
        BlobId blobId = BlobId.of(bucketName, uniqueFilename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        // Upload the file
        Blob blob = storage.create(blobInfo, file.getBytes());

        // Return the public URL
        return blob.getMediaLink();
    }

    public byte[] downloadFile(String gcsUrl) {
        // Extract the object name from the GCS URL
        String objectName = extractObjectNameFromUrl(gcsUrl);

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        if (blob == null) {
            throw new IllegalArgumentException("File not found: " + objectName);
        }
        return blob.getContent();
    }

    public void deleteFile(String gcsUrl) {
        // Extract the object name from the GCS URL
        String objectName = extractObjectNameFromUrl(gcsUrl);

        storage.delete(BlobId.of(bucketName, objectName));
    }

    private String extractObjectNameFromUrl(String gcsUrl) {
        try {
            // For URLs like: https://storage.googleapis.com/download/storage/v1/b/bucket-name/o/filename.jpg?generation=123&alt=media
            if (gcsUrl.contains("/o/")) {
                int startIndex = gcsUrl.indexOf("/o/") + 3;
                int endIndex = gcsUrl.indexOf("?", startIndex);

                if (endIndex < 0) {
                    endIndex = gcsUrl.length();
                }

                String encodedFileName = gcsUrl.substring(startIndex, endIndex);
                return java.net.URLDecoder.decode(encodedFileName, "UTF-8");
            }

            // For direct object names or other URL formats
            return gcsUrl;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid GCS URL format: " + gcsUrl);
        }
    }

    public boolean fileExists(String gcsUrl) {
        try {
            String objectName = extractObjectNameFromUrl(gcsUrl);
            return storage.get(BlobId.of(bucketName, objectName)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
