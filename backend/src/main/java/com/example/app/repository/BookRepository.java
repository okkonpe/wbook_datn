package com.example.app.repository;

import com.example.app.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    // JpaSpecificationExecutor đã cung cấp findAll(Specification, Pageable)
    
    List<Book> findBySanPhamId(Integer sanPhamId);
    
    Book findByMaSanPhamChiTiet(String maSanPhamChiTiet);
    
    Book findByIsbn(String isbn);

    @Query("SELECT b FROM Book b ORDER BY b.soLuong ASC")
    List<Book> findLowStock(org.springframework.data.domain.Pageable pageable);
}