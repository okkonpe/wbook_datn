package com.example.app.controller;

import com.example.app.dto.sanPhamSachDTO.SanPhamDTO;
import com.example.app.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/san-pham")
public class SanPhamController {
    @Autowired
    SanPhamService sanPhamService;

    @GetMapping()
    public ResponseEntity<Page<SanPhamDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<SanPhamDTO> create(@RequestBody SanPhamDTO sanPhamDTO) {
        SanPhamDTO saved = sanPhamService.save(sanPhamDTO);
        return ResponseEntity.status(201).body(saved);
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
}
