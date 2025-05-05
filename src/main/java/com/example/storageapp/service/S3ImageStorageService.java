// service/S3ImageStorageService.java
package com.example.storageapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service("s3Storage")
public class S3ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3ImageStorageService(
            @Value("${app.storage.s3.bucket}") String bucketName,
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.s3.endpoint:}") String endpoint) {
        this.bucketName = bucketName;
        
        var builder = S3Client.builder()
                .region(Region.of(region));
        
        // If endpoint is provided, use it (for custom endpoints or different regions)
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        
        this.s3Client = builder.build();
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String key = file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public String uploadImageWithName(MultipartFile file, String name) {
        try {
            String key = name;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public Resource downloadImage(String name) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(name)
                    .build();

            var response = s3Client.getObject(getObjectRequest);
            byte[] data = response.readAllBytes();
            return new ByteArrayResource(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
}
