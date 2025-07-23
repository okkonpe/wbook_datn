package com.example.app.controller;

import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {
@Autowired
    KhachHangService khachHangService;
    @Autowired
    HoaDonMapper hoaDonMapper;
    @GetMapping("/acitivity")
    public ResponseEntity<List<ListDonHangDTO>> getDonHangCuaKhachHang(@AuthenticationPrincipal KhachHangUserDetails user) {
        String username = user.getUsername();
        List<ListDonHangDTO> danhSach = khachHangService.layDonHangTheoKhachHang(username);
        return ResponseEntity.ok(danhSach);
    }

}
