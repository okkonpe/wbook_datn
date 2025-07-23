package com.example.app.controller;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    HoaDonService hoaDonService;
    @GetMapping("/don-hang")
    public ResponseEntity<Page<ListDonHangDTO>> getAlldonHang( @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ListDonHangDTO> result = hoaDonService.getDonHang(pageable);
        return ResponseEntity.ok(result);    }

    @GetMapping("/chi-tiet/{id}")
    public ResponseEntity<List<ListGioHangDTO>> getChiTietHD(@PathVariable Integer id){
        List<ListGioHangDTO> hd = hoaDonService.getListItemHD(id);
        return  ResponseEntity.ok(hd);
    } 
    @PutMapping("/thanh-toan")
    public ResponseEntity<HoaDonRequestDTO> thanhToan(@RequestBody HoaDonRequestDTO dto){
        System.out.println(dto.getKhachHangID());
        HoaDonRequestDTO hd = hoaDonService.thanhToanCOD(dto);
        return  ResponseEntity.ok(hd);
    }
    @PutMapping("/cap-nhat-trang-thai/dang-giao-hang/{id}")
    public ResponseEntity<ListDonHangDTO> dangGiaoHang(@PathVariable Integer id){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDangGiaoHang(id));
    }
    @PutMapping("/cap-nhat-trang-thai/da-giao-hang/{id}")
    public ResponseEntity<ListDonHangDTO> daGiaoHang(@PathVariable Integer id){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDaGiaoHang(id));
    }
    @PutMapping("/cap-nhat-trang-thai/da-huy/{id}")
    public ResponseEntity<ListDonHangDTO> daHuy(@PathVariable Integer id){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDaHuy(id));
    }

}
