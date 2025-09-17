package com.example.app.controller;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.VariantCreateDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.service.BookService;
import com.example.app.service.QRCodeService;
import com.example.app.service.UploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

//    @GetMapping()
//    public ResponseEntity<List<ListAllBookDTO>> getAllBooks() {
//        List<ListAllBookDTO> books = bookService.getAllBook();
//        return ResponseEntity.ok(books);
//    }

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
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File rỗng");
            }
            String filename = uploadService.saveFile(file);
            System.out.println("✅ Upload thành công: " + filename);
            return ResponseEntity.ok(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi không xác định: " + e.getMessage());
        }
    }
}