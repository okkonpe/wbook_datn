package com.example.app.controller;

import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<ListAllBookDTO>> getAllBooks() {
        List<ListAllBookDTO> books = bookService.getAllBook();
        return ResponseEntity.ok(books);
    }
}
