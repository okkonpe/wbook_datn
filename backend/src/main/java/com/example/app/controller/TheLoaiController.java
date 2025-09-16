package com.example.app.controller;

import com.example.app.dto.theLoaiDTO.TheLoaiDTO;
import com.example.app.service.TheLoaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/the-loai")
public class TheLoaiController {
    @Autowired
    TheLoaiService theLoaiService;

    @GetMapping()
    public ResponseEntity<Page<TheLoaiDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(theLoaiService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<TheLoaiDTO> create(TheLoaiDTO theLoaiDTO) {
        TheLoaiDTO saved = theLoaiService.save(theLoaiDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public TheLoaiDTO update(@PathVariable Integer id, @RequestBody TheLoaiDTO theLoaiDTO) {
        return theLoaiService.update(theLoaiDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        theLoaiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
