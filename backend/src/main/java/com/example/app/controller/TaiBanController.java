package com.example.app.controller;

import com.example.app.dto.TaiBanDTO;
import com.example.app.service.TaiBanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tai-ban")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TaiBanController {
    
    @Autowired
    private TaiBanService taiBanService;
    
    @GetMapping
    public ResponseEntity<List<TaiBanDTO>> getAllTaiBan() {
        System.out.println("TaiBanController.getAllTaiBan() called");
        try {
            List<TaiBanDTO> taiBans = taiBanService.getAllTaiBan();
            System.out.println("Returning " + taiBans.size() + " taiBans");
            return ResponseEntity.ok(taiBans);
        } catch (Exception e) {
            System.err.println("Error in getAllTaiBan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaiBanDTO> getTaiBanById(@PathVariable Integer id) {
        TaiBanDTO taiBan = taiBanService.getTaiBanById(id);
        if (taiBan != null) {
            return ResponseEntity.ok(taiBan);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<TaiBanDTO> createTaiBan(@RequestBody TaiBanDTO taiBanDTO) {
        System.out.println("TaiBanController.createTaiBan() called with: " + taiBanDTO);
        try {
            TaiBanDTO createdTaiBan = taiBanService.createTaiBan(taiBanDTO);
            System.out.println("Created taiBan: " + createdTaiBan);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTaiBan);
        } catch (RuntimeException e) {
            System.err.println("Error in createTaiBan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Unexpected error in createTaiBan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaiBanDTO> updateTaiBan(@PathVariable Integer id, @RequestBody TaiBanDTO taiBanDTO) {
        TaiBanDTO updatedTaiBan = taiBanService.updateTaiBan(id, taiBanDTO);
        if (updatedTaiBan != null) {
            return ResponseEntity.ok(updatedTaiBan);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaiBan(@PathVariable Integer id) {
        boolean deleted = taiBanService.deleteTaiBan(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/lan/{lanTaiBan}")
    public ResponseEntity<List<TaiBanDTO>> findByLanTaiBan(@PathVariable Integer lanTaiBan) {
        List<TaiBanDTO> taiBans = taiBanService.findByLanTaiBan(lanTaiBan);
        return ResponseEntity.ok(taiBans);
    }
    
    @GetMapping("/nam/{namTaiBan}")
    public ResponseEntity<List<TaiBanDTO>> findByNamTaiBan(@PathVariable Integer namTaiBan) {
        List<TaiBanDTO> taiBans = taiBanService.findByNamTaiBan(namTaiBan);
        return ResponseEntity.ok(taiBans);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaiBanDTO>> findByLanTaiBanAndNamTaiBan(
            @RequestParam(required = false) Integer lanTaiBan,
            @RequestParam(required = false) Integer namTaiBan) {
        
        List<TaiBanDTO> taiBans;
        if (lanTaiBan != null && namTaiBan != null) {
            taiBans = taiBanService.findByLanTaiBanAndNamTaiBan(lanTaiBan, namTaiBan);
        } else if (lanTaiBan != null) {
            taiBans = taiBanService.findByLanTaiBan(lanTaiBan);
        } else if (namTaiBan != null) {
            taiBans = taiBanService.findByNamTaiBan(namTaiBan);
        } else {
            taiBans = taiBanService.getAllTaiBan();
        }
        
        return ResponseEntity.ok(taiBans);
    }
}
