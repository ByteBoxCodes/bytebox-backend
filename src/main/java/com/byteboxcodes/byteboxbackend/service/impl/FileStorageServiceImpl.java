package com.byteboxcodes.byteboxbackend.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.byteboxcodes.byteboxbackend.service.FileStorageService;
import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "bytebox/avatars"));

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }
    }
}