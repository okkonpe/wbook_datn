package com.example.app.service;

import com.example.app.entity.HinhAnh;
import com.example.app.repository.HinhAnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {
    @Value("${upload.dir:src/main/resources/images}")
    private String UPLOAD_DIR;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    public HinhAnh saveFile(MultipartFile file) throws IOException {
        // Kiểm tra tệp rỗng
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được rỗng");
        }

        // Kiểm tra loại tệp (chỉ cho phép ảnh)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ hỗ trợ tệp ảnh (jpg, png, ...)");
        }

        // Kiểm tra kích thước tệp (giới hạn 25MB)
        if (file.getSize() > 25 * 1024 * 1024) {
            throw new IllegalArgumentException("Tệp quá lớn, tối đa 25MB");
        }

        // Vệ sinh tên tệp
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "_" + originalFilename;

        // Tạo đường dẫn lưu trữ
        Path path = Paths.get(UPLOAD_DIR, filename);
        Files.createDirectories(path.getParent());

        // Lưu tệp
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Lưu vào bảng hinh_anh
        HinhAnh hinhAnh = new HinhAnh();
        hinhAnh.setMaHinhAnh(UUID.randomUUID().toString().substring(0, 15));
        hinhAnh.setHinhAnh(filename);
        hinhAnh = hinhAnhRepository.save(hinhAnh);

        return hinhAnh;
    }
}