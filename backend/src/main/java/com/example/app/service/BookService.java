package com.example.app.service;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.VariantCreateDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.entity.*;
import com.example.app.mapper.BookMapper;
import com.example.app.repository.BookRepository;
import com.example.app.repository.*;
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
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    public List<ListAllBookDTO> getAllBook() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::listAllBookToDTO).collect(Collectors.toList());
    }

    public Page<ListAllBookDTO> getBooks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (keyword != null && !keyword.isEmpty()) {
            Specification<Book> spec = (root, query, criteriaBuilder) -> {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maSanPhamChiTiet")), likePattern)
                );
            };
            Page<Book> books = bookRepository.findAll(spec, pageable);
            return books.map(bookMapper::listAllBookToDTO);
        } else {
            Page<Book> books = bookRepository.findAll(pageable);
            return books.map(bookMapper::listAllBookToDTO);
        }
    }

    public BookDetailDTO getByID(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return bookMapper.getBookByIDDTO(book);
    }

    public List<BookDetailDTO> getByProductId(Integer productId) {
        List<Book> books = bookRepository.findBySanPhamId(productId);
        return books.stream()
                .map(bookMapper::getBookByIDDTO)
                .collect(Collectors.toList());
    }

    public BookDetailDTO findByCodeOrIsbn(String query) {
        // First try to find by product code (maSanPhamChiTiet)
        Book book = bookRepository.findByMaSanPhamChiTiet(query);
        
        // If not found, try to find by ISBN
        if (book == null) {
            book = bookRepository.findByIsbn(query);
        }
        
        if (book == null) {
            throw new RuntimeException("Product not found with code or ISBN: " + query);
        }
        
        return bookMapper.getBookByIDDTO(book);
    }

    public BookDetailDTO create(BookDetailDTO dto) {
        Book book = bookMapper.bookDetailDtoToEntity(dto);
        book = bookRepository.save(book);
        return bookMapper.getBookByIDDTO(book);
    }

    // ================== BULK CREATE (VARIANTS) ==================
    @org.springframework.beans.factory.annotation.Autowired private SanPhamRepository sanPhamRepository;
    @org.springframework.beans.factory.annotation.Autowired private TheLoaiRepository theLoaiRepository;
    @org.springframework.beans.factory.annotation.Autowired private NhaXuatBanRepository nhaXuatBanRepository;
    @org.springframework.beans.factory.annotation.Autowired private KichThuocRepository kichThuocRepository;
    @org.springframework.beans.factory.annotation.Autowired private LoaiBiaRepository loaiBiaRepository;
    @org.springframework.beans.factory.annotation.Autowired private LoaiGiayRepository loaiGiayRepository;
    @org.springframework.beans.factory.annotation.Autowired private HinhAnhRepository hinhAnhRepository;

    public java.util.List<BookDetailDTO> createBulkVariants(java.util.List<VariantCreateDTO> dtos) {
        java.util.List<Book> toSave = new java.util.ArrayList<>();
        for (VariantCreateDTO dto : dtos) {
            Book b = new Book();
            b.setSanPham(sanPhamRepository.findById(dto.getSanPhamId()).orElseThrow());
            b.setIsbn(dto.getIsbn());
            b.setMaSanPhamChiTiet(dto.getMaSanPhamChiTiet());
            if (dto.getTheLoaiId() != null)
                b.setTheLoai(theLoaiRepository.findById(dto.getTheLoaiId()).orElse(null));
            if (dto.getNhaXuatBanId() != null)
                b.setNhaXuatBan(nhaXuatBanRepository.findById(dto.getNhaXuatBanId()).orElse(null));
            if (dto.getKichThuocId() != null)
                b.setKichThuoc(kichThuocRepository.findById(dto.getKichThuocId()).orElse(null));
            if (dto.getLoaiBiaId() != null)
                b.setLoaiBia(loaiBiaRepository.findById(dto.getLoaiBiaId()).orElse(null));
            if (dto.getLoaiGiayId() != null)
                b.setLoaiGiay(loaiGiayRepository.findById(dto.getLoaiGiayId()).orElse(null));
            b.setSoTrang(dto.getSoTrang());
            b.setSoLanTaiBan(dto.getSoLanTaiBan());
            b.setKhoiLuongTinh(dto.getKhoiLuongTinh());
            b.setSoLuong(dto.getSoLuong());
            b.setNgayXuatBan(dto.getNgayXuatBan());
            if (dto.getHinhAnh() != null && !dto.getHinhAnh().isBlank()) {
                HinhAnh ha = new HinhAnh();
                // ma_hinh_anh bắt buộc -> tự sinh mã ngắn gọn
                String ma = "HA" + Integer.toHexString((int)(Math.random()*100000));
                ha.setMaHinhAnh(ma);
                ha.setHinhAnh(sanitizeFilename(dto.getHinhAnh()));
                ha = hinhAnhRepository.save(ha);
                b.setHinhAnh(ha);
            }
            b.setDonGia(dto.getDonGia());
            b.setMoTa(dto.getMoTa());
            b.setTrangThai(Boolean.TRUE.equals(dto.getTrangThai()));
            toSave.add(b);
        }
        java.util.List<Book> saved = bookRepository.saveAll(toSave);
        return saved.stream().map(bookMapper::getBookByIDDTO).collect(java.util.stream.Collectors.toList());
    }

    public BookDetailDTO update(Integer id, BookDetailDTO dto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        // Update fields
        if (dto.getIsbn() != null) existingBook.setIsbn(dto.getIsbn());
        if (dto.getMaSanPhamChiTiet() != null) existingBook.setMaSanPhamChiTiet(dto.getMaSanPhamChiTiet());
        if (dto.getSoTrang() != null) existingBook.setSoTrang(dto.getSoTrang());
        if (dto.getSoLanTaiBan() != null) existingBook.setSoLanTaiBan(dto.getSoLanTaiBan());
        if (dto.getKhoiLuongTinh() != null) existingBook.setKhoiLuongTinh(dto.getKhoiLuongTinh());
        if (dto.getSoLuong() != null) existingBook.setSoLuong(dto.getSoLuong());
        if (dto.getNgayXuatBan() != null) existingBook.setNgayXuatBan(dto.getNgayXuatBan());
        if (dto.getDonGia() != null) existingBook.setDonGia(dto.getDonGia());
        if (dto.getMoTa() != null) existingBook.setMoTa(dto.getMoTa());
        if (dto.getTrangThai() != null) existingBook.setTrangThai(dto.getTrangThai());

        // Save and return updated entity
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.getBookByIDDTO(updatedBook);
    }

    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    public void updateStatus(Integer id, Boolean status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        book.setTrangThai(status);
        bookRepository.save(book);
    }

    // Helper method to sanitize filenames to prevent DB column length issues
    private String sanitizeFilename(String filename) {
        if (filename == null) return null;
        
        // If filename is too long, truncate it
        if (filename.length() > 200) {
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex > 0) {
                String extension = filename.substring(dotIndex);
                return filename.substring(0, Math.min(200 - extension.length(), dotIndex)) + extension;
            } else {
                return filename.substring(0, 200);
            }
        }
        
        return filename;
    }
}