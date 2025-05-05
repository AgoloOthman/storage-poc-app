package com.example.storageapp.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface ImageStorageService {
    String uploadImage(MultipartFile file);
    String uploadImageWithName(MultipartFile file, String name);
    Resource downloadImage(String filename);
}
