package com.example.app.controller;

import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.entity.HoaDon;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
@CrossOrigin(origins = "http://localhost:4200")  // 👈 THÊM CORS
public class KhachHangController {

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    HoaDonMapper hoaDonMapper;

    // 👈 THÊM CÁC ENDPOINT CRUD
    @GetMapping
    public ResponseEntity<Page<KhachHangInfoDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(khachHangService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<KhachHangInfoDTO> create(@RequestBody KhachHangInfoDTO dto) {
        try {
            KhachHangInfoDTO saved = khachHangService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhachHangInfoDTO> update(@PathVariable Integer id, @RequestBody KhachHangInfoDTO dto) {
        try {
            KhachHangInfoDTO updated = khachHangService.update(dto, id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            khachHangService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Giữ nguyên các endpoint cũ
    @GetMapping("/acitivity")
    public ResponseEntity<List<ListDonHangDTO>> getDonHangCuaKhachHang(@AuthenticationPrincipal KhachHangUserDetails user) {
        String username = user.getUsername();
        List<ListDonHangDTO> danhSach = khachHangService.layDonHangTheoKhachHang(username);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/info")
    public ResponseEntity<KhachHangInfoDTO> getInfoKH(@AuthenticationPrincipal KhachHangUserDetails user) {
        String username = user.getUsername();
        KhachHangInfoDTO info = khachHangService.thongTinKhachHang(username);
        return ResponseEntity.ok(info);
    }
}
