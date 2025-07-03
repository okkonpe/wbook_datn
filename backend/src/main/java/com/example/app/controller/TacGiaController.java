package com.example.app.controller;

import com.example.app.dto.tacGiaDTO.TacGiaDTO;
import com.example.app.service.TacGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tac-gia")
public class TacGiaController {
    @Autowired
    TacGiaService tacGiaService;

    @GetMapping()
    public ResponseEntity<Page<TacGiaDTO>> getAll(Pageable pageable){
        return ResponseEntity.ok(tacGiaService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<TacGiaDTO> create(@RequestBody TacGiaDTO tacGiaDTO){
        TacGiaDTO saved = tacGiaService.save(tacGiaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public TacGiaDTO update(@PathVariable Integer id,@RequestBody TacGiaDTO tacGiaDTO){

        return tacGiaService.update(tacGiaDTO,id);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        tacGiaService.delete(id);
    }
}
