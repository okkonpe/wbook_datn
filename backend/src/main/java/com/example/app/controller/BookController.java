package com.example.app.controller;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.VariantCreateDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.repository.BookRepository;
import com.example.app.service.BookService;
import com.example.app.service.QRCodeService;
import com.example.app.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private UploadService uploadService;
    
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private BookRepository bookRepository;

//    @GetMapping()
//    public ResponseEntity<List<ListAllBookDTO>> getAllBooks() {
//        List<ListAllBookDTO> books = bookService.getAllBook();
//        return ResponseEntity.ok(books);
//    }

    @GetMapping("")
    public ResponseEntity<Page<ListAllBookDTO>> getBooks(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<ListAllBookDTO> result = bookService.getBooks(keyword, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/theloai/{id}")
    public Page<ListAllBookDTO> getByTheLoai(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return bookService.getBookByTheLoai(page,size,id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getByID(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getByID(id));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<BookDetailDTO>> getByProductId(@PathVariable Integer productId) {
        try {
            List<BookDetailDTO> variants = bookService.getByProductId(productId);
            return ResponseEntity.ok(variants);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@PathVariable Integer id) {
        try {
            BookDetailDTO book = bookService.getByID(id);
            // Use simple format for QR scanning: just the product code
            String qrContent = book.getMaSanPhamChiTiet();
            
            byte[] png = qrCodeService.generateQRCodePng(qrContent, 320, 320);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr-" + book.getMaSanPhamChiTiet() + ".png")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(png);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<BookDetailDTO> searchProduct(@RequestParam String q) {
        try {
            // Search by product code (maSanPhamChiTiet) or ISBN
            BookDetailDTO product = bookService.findByCodeOrIsbn(q);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<BookDetailDTO> createBook(@RequestBody @Valid BookDetailDTO dto) {
        BookDetailDTO created = bookService.create(dto);
        return ResponseEntity.ok(created);
    }

    @RequestMapping(value = "/bulk-create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<java.util.List<BookDetailDTO>> createBulkVariants(@RequestBody java.util.List<VariantCreateDTO> dtos) {
        try {
            System.out.println("✅ Nhận request bulk-create với " + dtos.size() + " biến thể");
            java.util.List<BookDetailDTO> result = bookService.createBulkVariants(dtos);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Integer id, @RequestBody java.util.Map<String, Boolean> request) {
        Boolean trangThai = request.get("trangThai");
        bookService.updateStatus(id, trangThai);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        System.out.println("📤 Nhận request upload image: " + file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                System.err.println("❌ File rỗng");
                return ResponseEntity.badRequest().body("File rỗng");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.err.println("❌ File không phải là hình ảnh: " + contentType);
                return ResponseEntity.badRequest().body("File không phải là hình ảnh");
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                System.err.println("❌ File quá lớn: " + file.getSize() + " bytes");
                return ResponseEntity.badRequest().body("File quá lớn (tối đa 5MB)");
            }
            
            String filename = uploadService.saveFile(file);
            System.out.println("✅ Upload thành công: " + filename);
            return ResponseEntity.ok(filename);
        } catch (IOException e) {
            System.err.println("❌ Lỗi IO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/create-with-variant")
    public ResponseEntity<?> createBookWithVariant(@RequestBody CreateBookWithVariantDTO dto) {
        try {
            System.out.println("=== DEBUG: Tạo sách với biến thể F1 ===");
            System.out.println("DTO nhận được: " + dto);
            
            BookDetailDTO result = bookService.createBookWithVariant(dto);
            
            System.out.println("✅ Đã tạo sách với biến thể F1 thành công");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo sách với biến thể F1: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi khi tạo sách: " + e.getMessage());
        }
    }

    // DTO cho tạo sách với biến thể F1
    public static class CreateBookWithVariantDTO {
        // Product info
        private String tenSanPham;
        private String moTa;
        private Boolean trangThai;
        
        // Variant F1 info
        private String isbn;
        private String maSanPhamChiTiet;
        private Integer donGia;
        private Integer soLuong;
        private String ngayXuatBan;
        private List<Integer> taiBanIds;
        private Integer theLoaiId;
        private Integer nhaXuatBanId;
        private Integer kichThuocId;
        private Integer loaiBiaId;
        private Integer loaiGiayId;
        private Float khoiLuongTinh;
        private String moTaBienThe;
        private List<Integer> tacGiaIds;
        private List<Integer> chuDeIds;
        private Integer soTrang;
        private String hinhAnh;

        // Getters and Setters
        public String getTenSanPham() { return tenSanPham; }
        public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
        
        public String getMoTa() { return moTa; }
        public void setMoTa(String moTa) { this.moTa = moTa; }
        
        public Boolean getTrangThai() { return trangThai; }
        public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }
        
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        
        public String getMaSanPhamChiTiet() { return maSanPhamChiTiet; }
        public void setMaSanPhamChiTiet(String maSanPhamChiTiet) { this.maSanPhamChiTiet = maSanPhamChiTiet; }
        
        public Integer getDonGia() { return donGia; }
        public void setDonGia(Integer donGia) { this.donGia = donGia; }
        
        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
        
        public String getNgayXuatBan() { return ngayXuatBan; }
        public void setNgayXuatBan(String ngayXuatBan) { this.ngayXuatBan = ngayXuatBan; }
        
        public List<Integer> getTaiBanIds() { return taiBanIds; }
        public void setTaiBanIds(List<Integer> taiBanIds) { this.taiBanIds = taiBanIds; }
        
        public Integer getTheLoaiId() { return theLoaiId; }
        public void setTheLoaiId(Integer theLoaiId) { this.theLoaiId = theLoaiId; }
        
        public Integer getNhaXuatBanId() { return nhaXuatBanId; }
        public void setNhaXuatBanId(Integer nhaXuatBanId) { this.nhaXuatBanId = nhaXuatBanId; }
        
        public Integer getKichThuocId() { return kichThuocId; }
        public void setKichThuocId(Integer kichThuocId) { this.kichThuocId = kichThuocId; }
        
        public Integer getLoaiBiaId() { return loaiBiaId; }
        public void setLoaiBiaId(Integer loaiBiaId) { this.loaiBiaId = loaiBiaId; }
        
        public Integer getLoaiGiayId() { return loaiGiayId; }
        public void setLoaiGiayId(Integer loaiGiayId) { this.loaiGiayId = loaiGiayId; }
        
        public Float getKhoiLuongTinh() { return khoiLuongTinh; }
        public void setKhoiLuongTinh(Float khoiLuongTinh) { this.khoiLuongTinh = khoiLuongTinh; }
        
        public String getMoTaBienThe() { return moTaBienThe; }
        public void setMoTaBienThe(String moTaBienThe) { this.moTaBienThe = moTaBienThe; }
        
        public List<Integer> getTacGiaIds() { return tacGiaIds; }
        public void setTacGiaIds(List<Integer> tacGiaIds) { this.tacGiaIds = tacGiaIds; }
        
        public List<Integer> getChuDeIds() { return chuDeIds; }
        public void setChuDeIds(List<Integer> chuDeIds) { this.chuDeIds = chuDeIds; }
        
        public Integer getSoTrang() { return soTrang; }
        public void setSoTrang(Integer soTrang) { this.soTrang = soTrang; }
        
        public String getHinhAnh() { return hinhAnh; }
        public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    }
}