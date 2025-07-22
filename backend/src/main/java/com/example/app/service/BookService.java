package com.example.app.service;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.mapper.BookMapper;
import com.example.app.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    // ================== READ ==================
    public Page<ListAllBookDTO> getBooks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<Book> spec = Specification.where(null);

        if (StringUtils.hasText(keyword)) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);

                // Subquery cho tìm theo tên tác giả
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<Book> bookSubRoot = subquery.from(Book.class);
                Join<Object, Object> joinTacGia = bookSubRoot.join("tacGia");

                subquery.select(bookSubRoot.get("id"))
                        .where(
                                cb.equal(bookSubRoot.get("id"), root.get("id")),
                                cb.like(cb.lower(joinTacGia.get("tenTacGia")), "%" + keyword.toLowerCase() + "%")
                        );

                return cb.or(
                        cb.like(cb.lower(root.get("sanPham").get("tenSanPham")), "%" + keyword.toLowerCase() + "%"),
                        cb.exists(subquery)
                );
            });
        }


        return bookRepository.findAll(spec, pageable)
                .map(bookMapper::listAllBookToDTO);
    }

    public BookDetailDTO getByID(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::getBookByIDDTO)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sách với id = " + id));
    }

    public List<ListAllBookDTO> getAllBook() {
        List<Book> list = bookRepository.findAll();
        return list.stream().map(bookMapper::listAllBookToDTO).collect(Collectors.toList());
    }

    // ================== CREATE ==================
    public BookDetailDTO create(BookDetailDTO dto) {
        Book book = bookMapper.bookDetailDtoToEntity(dto);
        book = bookRepository.save(book);
        return bookMapper.getBookByIDDTO(book);
    }

    // ================== UPDATE ==================
    public BookDetailDTO update(Integer id, BookDetailDTO dto) {
        Optional<Book> existing = bookRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy sách với id = " + id);
        }

        Book book = bookMapper.bookDetailDtoToEntity(dto);
        book.setId(id); // Giữ ID cũ
        book = bookRepository.save(book);

        return bookMapper.getBookByIDDTO(book);
    }

    // ================== DELETE ==================
    public void delete(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy sách với id = " + id);
        }
        bookRepository.deleteById(id);
    }
}
