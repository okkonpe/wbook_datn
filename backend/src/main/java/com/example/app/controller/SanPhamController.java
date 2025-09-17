package com.example.app.controller;

import com.example.app.dto.sanPhamSachDTO.SanPhamDTO;
import com.example.app.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    private com.example.app.service.QRCodeService qrCodeService;

    @GetMapping()
    public ResponseEntity<Page<SanPhamDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<SanPhamDTO> create(@RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO saved = sanPhamService.save(sanPhamDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(sanPhamService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamDTO> update(@PathVariable Integer id, @RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO updated = sanPhamService.update(sanPhamDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sanPhamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@PathVariable Integer id) {
        SanPhamDTO sp = sanPhamService.getById(id);
        byte[] png = qrCodeService.generateQRCodePng(sp.getMaSanPham(), 320, 320);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr-" + sp.getMaSanPham() + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Integer id, @RequestBody java.util.Map<String, Boolean> request) {
        Boolean trangThai = request.get("trangThai");
        sanPhamService.updateStatus(id, trangThai);
        return ResponseEntity.ok().build();
    }
}
