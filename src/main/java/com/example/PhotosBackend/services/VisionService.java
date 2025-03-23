package com.example.PhotosBackend.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisionService {

    private ImageAnnotatorClient client;

    @PostConstruct
    public void initialize() throws IOException {
        // Load credentials from the Vision API JSON key file
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream("src/main/resources/thinking-pagoda-453620-u3-ad8f8e2c5b71.json"));

        // Initialize the Vision API client
        this.client = ImageAnnotatorClient.create(
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(() -> credentials)
                        .build());
    }

    public List<String> detectLabels(byte[] imageBytes) {
        List<String> labels = new ArrayList<>();
        try {
            Image image = Image.newBuilder().setContent(ByteString.copyFrom(imageBytes)).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .build();

            BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
            for (EntityAnnotation annotation : response.getResponses(0).getLabelAnnotationsList()) {
                labels.add(annotation.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return labels;
    }
}
