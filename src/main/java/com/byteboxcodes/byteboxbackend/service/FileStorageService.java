package com.byteboxcodes.byteboxbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file);
}