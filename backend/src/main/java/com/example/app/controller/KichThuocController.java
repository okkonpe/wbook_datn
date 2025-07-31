package com.example.app.controller;

import com.example.app.dto.kichThuocDTO.KichThuocDTO;
import com.example.app.service.KichThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kich-thuoc")
public class KichThuocController {
    @Autowired
    private KichThuocService kichThuocService;

    @GetMapping
    public ResponseEntity<Page<KichThuocDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(kichThuocService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<KichThuocDTO> create(@RequestBody KichThuocDTO kichThuocDTO) {
        KichThuocDTO saved = kichThuocService.save(kichThuocDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KichThuocDTO> update(@PathVariable Integer id, @RequestBody KichThuocDTO kichThuocDTO) {
        return ResponseEntity.ok(kichThuocService.update(kichThuocDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        kichThuocService.delete(id);
        return ResponseEntity.noContent().build();
    }
}