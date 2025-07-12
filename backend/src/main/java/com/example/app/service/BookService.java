package com.example.app.service;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.mapper.BookMapper;
import com.example.app.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
   private BookRepository bookRepository;
    @Autowired
   private BookMapper bookMapper;
    public List<ListAllBookDTO> getAllBook(){
        List<Book> list = bookRepository.findAll();

        System.out.println("BOOKS: " + list);
         List<ListAllBookDTO> dto =list.stream().map(book -> bookMapper.listAllBooktoDTO(book)).collect(Collectors.toList());
        System.out.println("BOOKS: " + dto);
   return dto;
    }
    public BookDetailDTO getByID(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::getBookByIDDTO)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sách với id = " + id));
    }




}
