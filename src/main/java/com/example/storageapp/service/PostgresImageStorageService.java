// service/PostgresImageStorageService.java
package com.example.storageapp.service;

import com.example.storageapp.model.ImageEntity;
import com.example.storageapp.repository.ImageRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("postgresStorage")
public class PostgresImageStorageService implements ImageStorageService {

    private final ImageRepository repo;

    public PostgresImageStorageService(ImageRepository repo) {
        this.repo = repo;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            ImageEntity entity = new ImageEntity();
            entity.setName(file.getOriginalFilename());
            entity.setData(file.getBytes());
            repo.save(entity);
            return entity.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadImageWithName(MultipartFile file, String name) {
        try {
            ImageEntity entity = new ImageEntity();
            entity.setName(name);
            entity.setData(file.getBytes());
            repo.save(entity);
            return entity.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource downloadImage(String filename) {
        return repo.findById(filename)
                .map(e -> new ByteArrayResource(e.getData()))
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}
