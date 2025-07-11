package com.example.app.controller;

import com.example.app.dto.nhanVienDTO.NhanVienDTO;
import com.example.app.service.NhanVienService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
public class NhanVienController {
    
    @Autowired
    private NhanVienService nhanVienService;
    
    @GetMapping
    public ResponseEntity<Page<NhanVienDTO>> getAllNhanVien(Pageable pageable) {
        Page<NhanVienDTO> nhanVienList = nhanVienService.getAllNhanVienPaged(pageable);
        return ResponseEntity.ok(nhanVienList);
    }
    
    @GetMapping("/list-all")
    public ResponseEntity<List<NhanVienDTO>> getAllNhanVienList() {
        List<NhanVienDTO> nhanVienList = nhanVienService.getAllNhanVien();
        return ResponseEntity.ok(nhanVienList);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<NhanVienDTO>> searchNhanVien(
            @RequestParam(required = false) String name, 
            Pageable pageable) {
        Page<NhanVienDTO> result = nhanVienService.searchNhanVienByName(name, pageable);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NhanVienDTO> getNhanVienById(@PathVariable Integer id) {
        NhanVienDTO nhanVienDTO = nhanVienService.getNhanVienById(id);
        return ResponseEntity.ok(nhanVienDTO);
    }
    
    @PostMapping
    public ResponseEntity<NhanVienDTO> createNhanVien(@Valid @RequestBody NhanVienDTO nhanVienDTO) {
        NhanVienDTO createdNhanVien = nhanVienService.createNhanVien(nhanVienDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNhanVien);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<NhanVienDTO> updateNhanVien(
            @PathVariable Integer id, 
            @Valid @RequestBody NhanVienDTO nhanVienDTO) {
        NhanVienDTO updatedNhanVien = nhanVienService.updateNhanVien(id, nhanVienDTO);
        return ResponseEntity.ok(updatedNhanVien);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNhanVien(@PathVariable Integer id) {
        nhanVienService.deleteNhanVien(id);
        return ResponseEntity.noContent().build();
    }
} 