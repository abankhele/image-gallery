package com.example.PhotosBackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Document(collection = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @MongoId
    private String id;

    private String userId;

    private String albumId;

    private String gcsUrl; //google cloud storage

    private Date uploadedOn = new Date();

    private List<String> tags;

    private long size;

    private String format;

}
