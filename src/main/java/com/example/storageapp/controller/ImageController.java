// controller/ImageController.java
package com.example.storageapp.controller;

import com.example.storageapp.service.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageStorageService storage;

    public ImageController(ImageStorageService storage) {
        this.storage = storage;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return storage.uploadImage(file);
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) {
        return ResponseEntity.ok(storage.downloadImage(name));
    }

    @PostMapping(value = "/benchmark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> benchmark(@RequestParam MultipartFile file) {
        List<Long> uploadTimes = new ArrayList<>();
        List<Long> downloadTimes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        logger.info("Starting benchmark: uploading 10 times...");
        // Upload 10 times with different names
        for (int i = 0; i < 10; i++) {
            String name = "bench_" + System.currentTimeMillis() + "_" + i + "_" + file.getOriginalFilename();
            names.add(name);
            long start = System.nanoTime();
            storage.uploadImageWithName(file, name); // You need to implement this in your service
            long end = System.nanoTime();
            long duration = end - start;
            uploadTimes.add(duration);
            logger.info("Upload {}: name={}, time={} ms", i + 1, name, duration / 1_000_000.0);
        }

        logger.info("Starting benchmark: downloading 10 times...");
        // Download 10 times
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            long start = System.nanoTime();
            storage.downloadImage(name);
            long end = System.nanoTime();
            long duration = end - start;
            downloadTimes.add(duration);
            logger.info("Download {}: name={}, time={} ms", i + 1, name, duration / 1_000_000.0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("upload_max_ms", uploadTimes.stream().mapToLong(t -> t).max().orElse(0) / 1_000_000.0);
        result.put("upload_min_ms", uploadTimes.stream().mapToLong(t -> t).min().orElse(0) / 1_000_000.0);
        result.put("upload_avg_ms", uploadTimes.stream().mapToLong(t -> t).average().orElse(0) / 1_000_000.0);
        result.put("download_max_ms", downloadTimes.stream().mapToLong(t -> t).max().orElse(0) / 1_000_000.0);
        result.put("download_min_ms", downloadTimes.stream().mapToLong(t -> t).min().orElse(0) / 1_000_000.0);
        result.put("download_avg_ms", downloadTimes.stream().mapToLong(t -> t).average().orElse(0) / 1_000_000.0);

        logger.info("Benchmark complete. Results: {}", result);

        return result;
    }
}
