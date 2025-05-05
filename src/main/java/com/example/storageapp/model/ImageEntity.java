// model/ImageEntity.java
package com.example.storageapp.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
@Data
public class ImageEntity {
    @Id
    private String name;

    @Lob
    private byte[] data;
}
