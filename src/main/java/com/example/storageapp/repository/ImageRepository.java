// repository/ImageRepository.java
package com.example.storageapp.repository;

import com.example.storageapp.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {}
