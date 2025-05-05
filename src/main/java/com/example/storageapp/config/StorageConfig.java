// config/StorageConfig.java
package com.example.storageapp.config;

import com.example.storageapp.service.ImageStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StorageConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.storage.backend", havingValue = "postgresStorage")
    public ImageStorageService postgresImageStorageService(ImageStorageService postgresStorage) {
        return postgresStorage;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.storage.backend", havingValue = "s3Storage")
    public ImageStorageService s3ImageStorageService(ImageStorageService s3Storage) {
        return s3Storage;
    }
}
