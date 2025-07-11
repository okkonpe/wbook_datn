package com.example.app.controller;

import com.example.app.dto.ChucVuDTO;
import com.example.app.service.ChucVuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chuc-vu")
public class ChucVuController {
    
    @Autowired
    private ChucVuService chucVuService;
    
    @GetMapping
    public ResponseEntity<Page<ChucVuDTO>> getAllChucVu(Pageable pageable) {
        Page<ChucVuDTO> chucVuList = chucVuService.getAllChucVuPaged(pageable);
        return ResponseEntity.ok(chucVuList);
    }
    
    @GetMapping("/list-all")
    public ResponseEntity<List<ChucVuDTO>> getAllChucVuList() {
        List<ChucVuDTO> chucVuList = chucVuService.getAllChucVu();
        return ResponseEntity.ok(chucVuList);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChucVuDTO> getChucVuById(@PathVariable Integer id) {
        ChucVuDTO chucVuDTO = chucVuService.getChucVuById(id);
        return ResponseEntity.ok(chucVuDTO);
    }
    
    @PostMapping
    public ResponseEntity<ChucVuDTO> createChucVu(@Valid @RequestBody ChucVuDTO chucVuDTO) {
        // Nếu không cung cấp mã chức vụ, tự động tạo mã
        if (chucVuDTO.getMaChucVu() == null || chucVuDTO.getMaChucVu().isEmpty()) {
            chucVuDTO.setMaChucVu(chucVuService.generateUniqueMaChucVu());
        }
        
        ChucVuDTO createdChucVu = chucVuService.createChucVu(chucVuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChucVu);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ChucVuDTO> updateChucVu(@PathVariable Integer id, @Valid @RequestBody ChucVuDTO chucVuDTO) {
        ChucVuDTO updatedChucVu = chucVuService.updateChucVu(id, chucVuDTO);
        return ResponseEntity.ok(updatedChucVu);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChucVu(@PathVariable Integer id) {
        chucVuService.deleteChucVu(id);
        return ResponseEntity.noContent().build();
    }
} 