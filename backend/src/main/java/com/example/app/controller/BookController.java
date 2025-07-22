package com.example.app.controller;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
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
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;
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
    public ResponseEntity<BookDetailDTO> createBook(@RequestBody @Valid BookDetailDTO dto) {
        BookDetailDTO created = bookService.create(dto);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookDetailDTO> updateBook(@PathVariable Integer id, @RequestBody @Valid BookDetailDTO dto) {
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
            String filename = uploadService.saveFile(file);
            return ResponseEntity.ok(filename); // frontend sẽ nhận filename để dùng tiếp
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        }
    }


}
