package com.example.app.controller;

import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.khachHangDTO.DoiMKDTO;
import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.KhachHang;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.service.KhachHangService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {
    @Autowired
    KhachHangService khachHangService;
    @Autowired
    HoaDonMapper hoaDonMapper;
    
    @GetMapping("/acitivity")
    public ResponseEntity<List<ListDonHangDTO>> getDonHangCuaKhachHang(@AuthenticationPrincipal KhachHangUserDetails user)  {
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

    // Endpoint cho admin lấy danh sách khách hàng
    @GetMapping
    public ResponseEntity<Page<KhachHang>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> customers = khachHangService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    // ===== CRUD cho màn khách hàng (sử dụng KhachHangInfoDTO) =====
    @PostMapping
    public ResponseEntity<KhachHangInfoDTO> create(@RequestBody KhachHangInfoDTO dto) {
        KhachHangInfoDTO saved = khachHangService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhachHangInfoDTO> update(@PathVariable Integer id, @RequestBody KhachHangInfoDTO dto) {
        KhachHangInfoDTO updated = khachHangService.update(dto, id);
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/updateInfo")
    public ResponseEntity<KhachHangInfoDTO> updateInfo(@AuthenticationPrincipal KhachHangUserDetails user, @RequestBody KhachHangInfoDTO dto) {
        KhachHangInfoDTO updated = khachHangService.updateInfo(dto,user.getUsername());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody DoiMKDTO request,
                                            @AuthenticationPrincipal KhachHangUserDetails user) {
        khachHangService.changePassword(user.getUsername(), request);
            return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        khachHangService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint tạo khách hàng mới nhanh (không cần tài khoản)
    @PostMapping("/quick-create")
    public ResponseEntity<KhachHang> createQuickCustomer(@RequestBody QuickCustomerDTO dto) {
        KhachHang customer = khachHangService.createQuickCustomer(dto);
        return ResponseEntity.ok(customer);
    }

    // DTO cho tạo khách hàng nhanh
    public static class QuickCustomerDTO {
        private String tenKhachHang;
        private String sdt;
        private String diaChi;
        private String email;

        // Getters and Setters
        public String getTenKhachHang() { return tenKhachHang; }
        public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
        
        public String getSdt() { return sdt; }
        public void setSdt(String sdt) { this.sdt = sdt; }
        
        public String getDiaChi() { return diaChi; }
        public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
