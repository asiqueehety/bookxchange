package com.example.bookxchange.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    @Value("${app.upload.dir:uploads/profiles}")
    private String uploadDir;

    @Value("${app.upload.max-size:5242880}")
    private long maxFileSize;

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(
        Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp")
    );

    /**
     * Save uploaded file and return the filename
     */
    public String saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, GIF, WebP) are allowed");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            log.info("File saved successfully: {}", filename);
            return filename;
        } catch (IOException e) {
            log.error("Error saving file: {}", e.getMessage());
            throw new Exception("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Delete file by filename
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", filename);
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
        }
    }

    /**
     * Get file path for serving
     */
    public String getFileUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        return "/uploads/profiles/" + filename;
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(uploadDir).resolve(filename));
    }
}
