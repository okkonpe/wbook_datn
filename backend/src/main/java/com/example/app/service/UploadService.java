package com.example.app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {
    private final String UPLOAD_DIR = "uploads";
    public String saveFile(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot); 
            }
        }
        String filename = UUID.randomUUID().toString() + ext;
        

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("Đã tạo thư mục: " + uploadDir.getAbsolutePath());
        }
        
        Path path = Paths.get(UPLOAD_DIR, filename);
        System.out.println("Đường dẫn lưu file: " + path.toAbsolutePath());
        
        try {
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Lưu file thành công: " + filename);
            return filename;
        } catch (Exception e) {
            System.err.println("Lỗi lưu file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
