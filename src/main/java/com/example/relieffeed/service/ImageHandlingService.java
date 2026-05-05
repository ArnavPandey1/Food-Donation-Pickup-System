package com.example.relieffeed.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImageHandlingService {

    // Stub for uploading image
    public String uploadImage(MultipartFile file) {
        // In a real scenario, upload to AWS S3, Cloudinary, etc.
        // Return a mock URL for now
        return "https://dummy-image-store.com/images/" + UUID.randomUUID().toString() + ".jpg";
    }
}
