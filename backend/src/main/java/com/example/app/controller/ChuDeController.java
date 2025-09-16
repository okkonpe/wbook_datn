package com.example.app.controller;

import com.example.app.dto.chuDeDTO.ChuDeDTO;
import com.example.app.service.ChuDeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chu-de")
public class ChuDeController {
    @Autowired
    ChuDeService chuDeService;

    @GetMapping()
    public ResponseEntity<Page<ChuDeDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(chuDeService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<ChuDeDTO> create(@RequestBody ChuDeDTO chuDeDTO) {
        ChuDeDTO saved = chuDeService.save(chuDeDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChuDeDTO> update(@PathVariable Integer id, @RequestBody ChuDeDTO chuDeDTO) {
        ChuDeDTO updated = chuDeService.update(chuDeDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        chuDeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
