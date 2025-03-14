package com.example.PhotosBackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "album")
public class Album {
    @MongoId
    private String albumid;

    private String userId;

    private String name;

    private Date createdOn = new Date();

    private List<String> photoIds;
}
