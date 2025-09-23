package com.example.app.service;

import com.example.app.controller.BookController;
import com.example.app.dto.TaiBanDTO;
import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.VariantCreateDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.entity.*;
import com.example.app.mapper.BookMapper;
import com.example.app.repository.BookRepository;
import com.example.app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private LoaiBiaRepository loaiBiaRepository;

    @Autowired
    private LoaiGiayRepository loaiGiayRepository;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private TaiBanRepository taiBanRepository;

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private ChuDeRepository chuDeRepository;

    public List<ListAllBookDTO> getAllBook() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::listAllBookToDTO).collect(Collectors.toList());
    }
    public Page<ListAllBookDTO> getBookByTheLoai(int page, int size,Integer theLoaiID) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Book> books = bookRepository.findByTheLoai_Id(theLoaiID, pageable);
        return books.map(bookMapper::listAllBookToDTO);
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
        System.out.println("📖 === LẤY THÔNG TIN SẢN PHẨM ===");
        System.out.println("📦 Sản phẩm ID: " + id);
        System.out.println("📊 Số lượng tồn kho: " + book.getSoLuong() + " (Mã: " + book.getMaSanPhamChiTiet() + ")");
        BookDetailDTO dto = bookMapper.getBookByIDDTO(book);
        System.out.println("✅ Trả về DTO với số lượng: " + dto.getSoLuong());
        return dto;
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

            b.setKhoiLuongTinh(dto.getKhoiLuongTinh());
            
            // Set tái bản
            if (dto.getTaiBanIds() != null && !dto.getTaiBanIds().isEmpty()) {
                Set<TaiBan> taiBans = new HashSet<>();
                for (Integer taiBanId : dto.getTaiBanIds()) {
                    TaiBan taiBan = taiBanRepository.findById(taiBanId).orElse(null);
                    if (taiBan != null) {
                        taiBans.add(taiBan);
                    }
                }
                b.setTaiBans(taiBans);
            }
            b.setSoLuong(dto.getSoLuong());
            b.setNgayXuatBan(dto.getNgayXuatBan());
            if (dto.getHinhAnh() != null && !dto.getHinhAnh().isBlank()) {
                HinhAnh ha = new HinhAnh();
                // ma_hinh_anh bắt buộc -> tự sinh mã ngắn gọn
                String ma = "HA" + Integer.toHexString((int)(Math.random()*100000));
                ha.setMaHinhAnh(ma);
                // Chỉ lưu tên file, không lưu URL đầy đủ
                String filename = dto.getHinhAnh();
                if (filename.contains("/")) {
                    filename = filename.substring(filename.lastIndexOf("/") + 1);
                }
                ha.setHinhAnh(sanitizeFilename(filename));
                ha = hinhAnhRepository.save(ha);
                b.setHinhAnh(ha);
            }
            b.setDonGia(dto.getDonGia());
            b.setMoTa(dto.getMoTa());
            b.setTrangThai(Boolean.TRUE.equals(dto.getTrangThai()));
            
            // Set tác giả
            if (dto.getTacGiaIds() != null && !dto.getTacGiaIds().isEmpty()) {
                Set<TacGia> tacGias = new HashSet<>();
                for (Integer tacGiaId : dto.getTacGiaIds()) {
                    TacGia tacGia = tacGiaRepository.findById(tacGiaId).orElse(null);
                    if (tacGia != null) {
                        tacGias.add(tacGia);
                    }
                }
                b.setTacGia(tacGias);
            }
            
            // Set chủ đề
            if (dto.getChuDeIds() != null && !dto.getChuDeIds().isEmpty()) {
                Set<ChuDe> chuDes = new HashSet<>();
                for (Integer chuDeId : dto.getChuDeIds()) {
                    ChuDe chuDe = chuDeRepository.findById(chuDeId).orElse(null);
                    if (chuDe != null) {
                        chuDes.add(chuDe);
                    }
                }
                b.setChuDes(chuDes);
            }
            
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
        if (dto.getKhoiLuongTinh() != null) existingBook.setKhoiLuongTinh(dto.getKhoiLuongTinh());
        
        // Update tái bản - clear existing and set new ones
        if (dto.getTaiBans() != null) {
            existingBook.getTaiBans().clear();
            for (TaiBanDTO taiBanDTO : dto.getTaiBans()) {
                TaiBan taiBan = taiBanRepository.findById(taiBanDTO.getId()).orElse(null);
                if (taiBan != null) {
                    existingBook.getTaiBans().add(taiBan);
                }
            }
        }
        if (dto.getSoLuong() != null) existingBook.setSoLuong(dto.getSoLuong());
        if (dto.getNgayXuatBan() != null) existingBook.setNgayXuatBan(dto.getNgayXuatBan());
        if (dto.getDonGia() != null) existingBook.setDonGia(dto.getDonGia());
        if (dto.getMoTa() != null) existingBook.setMoTa(dto.getMoTa());
        if (dto.getTrangThai() != null) existingBook.setTrangThai(dto.getTrangThai());

        // Update theLoai if provided
        if (dto.getTheLoai() != null && !dto.getTheLoai().isBlank()) {
            TheLoai theLoai = theLoaiRepository.findAll().stream()
                .filter(tl -> tl.getTenTheLoai().equals(dto.getTheLoai()))
                .findFirst()
                .orElse(null);
            if (theLoai != null) {
                existingBook.setTheLoai(theLoai);
            }
        }

        // Update nhaXuatBan if provided
        if (dto.getNhaXuatBan() != null && !dto.getNhaXuatBan().isBlank()) {
            NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findAll().stream()
                .filter(nxb -> nxb.getTenNhaXuatBan().equals(dto.getNhaXuatBan()))
                .findFirst()
                .orElse(null);
            if (nhaXuatBan != null) {
                existingBook.setNhaXuatBan(nhaXuatBan);
            }
        }

        // Update image if provided
        if (dto.getHinhAnh() != null && !dto.getHinhAnh().isBlank()) {
            HinhAnh ha = new HinhAnh();
            // ma_hinh_anh bắt buộc -> tự sinh mã ngắn gọn
            String ma = "HA" + Integer.toHexString((int)(Math.random()*100000));
            ha.setMaHinhAnh(ma);
            ha.setHinhAnh(sanitizeFilename(dto.getHinhAnh()));
            ha = hinhAnhRepository.save(ha);
            existingBook.setHinhAnh(ha);
        }

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

    private String generateMaSanPham() {
        String prefix = "SP";
        String ma;
        int attempts = 0;
        do {
            // Sử dụng timestamp ngắn gọn để tránh vượt quá 15 ký tự
            long timestamp = System.currentTimeMillis() % 1000000; // Chỉ lấy 6 số cuối
            int random = (int) (Math.random() * 100);
            ma = prefix + String.format("%06d%02d", timestamp, random);
            attempts++;
            if (attempts > 10) { // Tránh infinite loop
                throw new RuntimeException("Không thể tạo mã sản phẩm duy nhất");
            }
        } while (sanPhamRepository.existsByMaSanPham(ma));
        return ma;
    }

    public BookDetailDTO createBookWithVariant(BookController.CreateBookWithVariantDTO dto) {
        try {
            System.out.println("=== DEBUG: Bắt đầu tạo sách với biến thể F1 ===");
            
            // 1. Tạo sản phẩm mới
            SanPham sanPham = new SanPham();
            sanPham.setMaSanPham(generateMaSanPham()); // Tự động tạo mã sản phẩm
            sanPham.setTenSanPham(dto.getTenSanPham());
            sanPham.setMoTa(dto.getMoTa());
            sanPham.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
            sanPham.setNgayTao(new Date()); // Set ngày tạo
            
            SanPham savedSanPham = sanPhamRepository.save(sanPham);
            System.out.println("✅ Đã tạo sản phẩm: " + savedSanPham.getId());
            
            // 2. Tạo biến thể F1
            Book book = new Book();
            book.setSanPham(savedSanPham);
            book.setIsbn(dto.getIsbn());
            book.setMaSanPhamChiTiet(dto.getMaSanPhamChiTiet());
            book.setDonGia(BigDecimal.valueOf(dto.getDonGia()));
            book.setSoLuong(dto.getSoLuong());
            book.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
            book.setMoTa(dto.getMoTaBienThe());
            
            // Set ngày xuất bản
            if (dto.getNgayXuatBan() != null && !dto.getNgayXuatBan().isEmpty()) {
                book.setNgayXuatBan(LocalDate.parse(dto.getNgayXuatBan()));
            }
            

            // Set tái bản
            if (dto.getTaiBanIds() != null && !dto.getTaiBanIds().isEmpty()) {
                Set<TaiBan> taiBans = new HashSet<>();
                for (Integer taiBanId : dto.getTaiBanIds()) {
                    TaiBan taiBan = taiBanRepository.findById(taiBanId).orElse(null);
                    if (taiBan != null) {
                        taiBans.add(taiBan);
                    }
                }
                book.setTaiBans(taiBans);
            }

            // Set khối lượng tịnh
            if (dto.getKhoiLuongTinh() != null) {
                book.setKhoiLuongTinh(dto.getKhoiLuongTinh());
            }
            
            // Set các entity liên quan
            if (dto.getTheLoaiId() != null) {
                TheLoai theLoai = theLoaiRepository.findById(dto.getTheLoaiId()).orElse(null);
                if (theLoai != null) {
                    book.setTheLoai(theLoai);
                }
            }
            
            if (dto.getNhaXuatBanId() != null) {
                NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(dto.getNhaXuatBanId()).orElse(null);
                if (nhaXuatBan != null) {
                    book.setNhaXuatBan(nhaXuatBan);
                }
            }
            
            if (dto.getKichThuocId() != null) {
                KichThuoc kichThuoc = kichThuocRepository.findById(dto.getKichThuocId()).orElse(null);
                if (kichThuoc != null) {
                    book.setKichThuoc(kichThuoc);
                }
            }
            
            if (dto.getLoaiBiaId() != null) {
                LoaiBia loaiBia = loaiBiaRepository.findById(dto.getLoaiBiaId()).orElse(null);
                if (loaiBia != null) {
                    book.setLoaiBia(loaiBia);
                }
            }
            
            if (dto.getLoaiGiayId() != null) {
                LoaiGiay loaiGiay = loaiGiayRepository.findById(dto.getLoaiGiayId()).orElse(null);
                if (loaiGiay != null) {
                    book.setLoaiGiay(loaiGiay);
                }
            }
            
            // Set tác giả
            if (dto.getTacGiaIds() != null && !dto.getTacGiaIds().isEmpty()) {
                Set<TacGia> tacGias = new HashSet<>();
                for (Integer tacGiaId : dto.getTacGiaIds()) {
                    TacGia tacGia = tacGiaRepository.findById(tacGiaId).orElse(null);
                    if (tacGia != null) {
                        tacGias.add(tacGia);
                    }
                }
                book.setTacGia(tacGias);
            }
            
            // Set chủ đề
            if (dto.getChuDeIds() != null && !dto.getChuDeIds().isEmpty()) {
                Set<ChuDe> chuDes = new HashSet<>();
                for (Integer chuDeId : dto.getChuDeIds()) {
                    ChuDe chuDe = chuDeRepository.findById(chuDeId).orElse(null);
                    if (chuDe != null) {
                        chuDes.add(chuDe);
                    }
                }
                book.setChuDes(chuDes);
            }
            
            // Set số trang
            if (dto.getSoTrang() != null) {
                book.setSoTrang(dto.getSoTrang());
            }
            
            // Set hình ảnh
            if (dto.getHinhAnh() != null && !dto.getHinhAnh().isEmpty()) {
                HinhAnh ha = new HinhAnh();
                String ma = "HA" + Integer.toHexString((int)(Math.random()*100000));
                ha.setMaHinhAnh(ma);
                // Chỉ lưu tên file, không lưu URL đầy đủ
                String filename = dto.getHinhAnh();
                if (filename.contains("/")) {
                    filename = filename.substring(filename.lastIndexOf("/") + 1);
                }
                ha.setHinhAnh(sanitizeFilename(filename));
                ha = hinhAnhRepository.save(ha);
                book.setHinhAnh(ha);
            }
            
            Book savedBook = bookRepository.save(book);
            System.out.println("✅ Đã tạo biến thể F1: " + savedBook.getId());
            
            // 3. Trả về DTO
            BookDetailDTO result = bookMapper.getBookByIDDTO(savedBook);
            System.out.println("✅ Hoàn thành tạo sách với biến thể F1");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo sách với biến thể F1: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo sách với biến thể F1: " + e.getMessage(), e);
        }
    }
}