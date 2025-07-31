package com.example.app.controller;

import com.example.app.dto.nhaXuatBanDTO.NhaXuatBanDTO;
import com.example.app.service.NhaXuatBanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nha-xuat-ban")
public class NhaXuatBanController {
    @Autowired
    NhaXuatBanService nhaXuatBanService;

    @GetMapping()
    public ResponseEntity<Page<NhaXuatBanDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(nhaXuatBanService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<NhaXuatBanDTO> create(@RequestBody NhaXuatBanDTO nhaXuatBanDTO) {
        NhaXuatBanDTO saved = nhaXuatBanService.save(nhaXuatBanDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhaXuatBanDTO> update(@PathVariable Integer id, @RequestBody NhaXuatBanDTO nhaXuatBanDTO) {
        NhaXuatBanDTO updated = nhaXuatBanService.update(nhaXuatBanDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        nhaXuatBanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
