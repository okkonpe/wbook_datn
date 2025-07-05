package com.example.app.controller;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("")
    public ResponseEntity<List<ListAllBookDTO>> getAllBooks() {
        List<ListAllBookDTO> books = bookService.getAllBook();
        return ResponseEntity.ok(books);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getByID(@PathVariable Integer id){
        return ResponseEntity.ok(bookService.getByID(id));
    }
}
