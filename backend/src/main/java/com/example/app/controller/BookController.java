package com.example.app.controller;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.HinhAnh;
import com.example.app.service.BookService;
import com.example.app.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UploadService uploadService;

    @GetMapping("")
    public ResponseEntity<Page<ListAllBookDTO>> getBooks(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ListAllBookDTO> result = bookService.getBooks(keyword, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getByID(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getByID(id));
    }

    @PostMapping("")
    public ResponseEntity<BookDetailDTO> createBook(
            @RequestPart("book") @Valid BookDetailDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (file != null && !file.isEmpty()) {
            try {
                HinhAnh hinhAnh = uploadService.saveFile(file);
                dto.setHinhAnh(hinhAnh.getHinhAnh()); // Lưu tên tệp vào DTO để ánh xạ
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body(null);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        BookDetailDTO created = bookService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDetailDTO> updateBook(
            @PathVariable Integer id,
            @RequestPart("book") @Valid BookDetailDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (file != null && !file.isEmpty()) {
            try {
                HinhAnh hinhAnh = uploadService.saveFile(file);
                dto.setHinhAnh(hinhAnh.getHinhAnh()); // Lưu tên tệp vào DTO để ánh xạ
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body(null);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        BookDetailDTO updated = bookService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            HinhAnh hinhAnh = uploadService.saveFile(file);
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String fileUrl = baseUrl + "/uploads/" + hinhAnh.getHinhAnh();
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}