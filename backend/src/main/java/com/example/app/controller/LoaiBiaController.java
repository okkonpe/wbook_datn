package com.example.app.controller;

import com.example.app.dto.loaiBiaDTO.LoaiBiaDTO;
import com.example.app.service.LoaiBiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loai-bia")
public class LoaiBiaController {
    @Autowired
    LoaiBiaService loaiBiaService;

    @GetMapping()
    public ResponseEntity<Page<LoaiBiaDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(loaiBiaService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<LoaiBiaDTO> create(@RequestBody LoaiBiaDTO loaiBiaDTO) {
        LoaiBiaDTO saved = loaiBiaService.save(loaiBiaDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoaiBiaDTO> update(@PathVariable Integer id, @RequestBody LoaiBiaDTO loaiBiaDTO) {
        LoaiBiaDTO updated = loaiBiaService.update(loaiBiaDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        loaiBiaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
