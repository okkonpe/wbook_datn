package com.example.app.controller;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.dto.banHangDTO.ThemGioHangDTO;
import com.example.app.entity.KhachHang;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {
    @Autowired
    private   HoaDonService hoaDonService;

        @GetMapping("/khach-hang")
    //    spring security @AuthenticationPrincipal
        public ResponseEntity<List<ListGioHangDTO>> getGioHangTheoKhachHang(@AuthenticationPrincipal KhachHangUserDetails khachHangUserDetails) {
            List<ListGioHangDTO> dtoList = hoaDonService.getGioHangByKhachHangId(khachHangUserDetails.getId());
            return ResponseEntity.ok(dtoList);
        }
    @PostMapping("/add")
    public ResponseEntity<ThemGioHangDTO> addToCart(@RequestBody ThemGioHangDTO themGioHangDTO){
       ThemGioHangDTO dto = hoaDonService.themGioHang(themGioHangDTO);
        return ResponseEntity.ok(dto);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> xoaKhoiGioHang(
            @RequestParam("khachHangId") Integer khachHangId,
            @RequestParam("idSanPham") Integer idSanPham
    ) {
        hoaDonService.xoaSanPhamKhoiGioHang(khachHangId, idSanPham);
        return ResponseEntity.noContent().build();
    }

}
