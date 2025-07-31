package com.example.app.controller;

import com.example.app.dto.loaiGiayDTO.LoaiGiayDTO;
import com.example.app.service.LoaiGiayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loai-giay")
public class LoaiGiayController {
    @Autowired
    LoaiGiayService loaiGiayService;

    @GetMapping()
    public ResponseEntity<Page<LoaiGiayDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(loaiGiayService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<LoaiGiayDTO> create(@RequestBody LoaiGiayDTO loaiGiayDTO) {
        LoaiGiayDTO saved = loaiGiayService.save(loaiGiayDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoaiGiayDTO> update(@PathVariable Integer id, @RequestBody LoaiGiayDTO loaiGiayDTO) {
        LoaiGiayDTO updated = loaiGiayService.update(loaiGiayDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        loaiGiayService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
