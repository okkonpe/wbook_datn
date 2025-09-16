package com.example.app.controller;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.NhanVien;
import com.example.app.entity.TrangThaiHoaDon;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.repository.HoaDonRepository;
import com.example.app.repository.NhanVienRepository;
import com.example.app.repository.TrangThaiHoaDonRepo;
import com.example.app.service.HoaDonService;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    TrangThaiHoaDonRepo trangThaiHoaDonRepo;
    @Autowired
    HoaDonMapper hoaDonMapper;
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @GetMapping("/order")
    public ResponseEntity<Page<ListDonHangDTO>> getAllOrderShipper( @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ListDonHangDTO> result = hoaDonService.getAllOrder(pageable);
        return ResponseEntity.ok(result);    }

    @GetMapping("/loc-tim-kiem")
    public ResponseEntity<Page<ListDonHangDTO>> locTimKiem(
            @RequestParam(required = false) String loaiTT,
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) String status,@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ListDonHangDTO> pagea = hoaDonRepository.searchHoaDon(loaiTT,status,maHoaDon,pageable).map(hoaDonMapper::donHangtoDTO);
        return ResponseEntity.ok(pagea);
    }


    @GetMapping("/chi-tiet/{id}")
    public ResponseEntity<List<ListGioHangDTO>> getChiTietHD(@PathVariable Integer id){
        List<ListGioHangDTO> hd = hoaDonService.getListItemHD(id);
        return  ResponseEntity.ok(hd);
    } 
    @PutMapping("/thanh-toan-cod")
    public ResponseEntity<HoaDonRequestDTO> thanhToan(@RequestBody HoaDonRequestDTO dto){
        System.out.println(dto.getKhachHangID());
        HoaDonRequestDTO hd = hoaDonService.thanhToanCOD(dto);
        return  ResponseEntity.ok(hd);
    }
    @PutMapping("/cap-nhat-trang-thai/da-xac-nhan/{id}")
    public ResponseEntity<ListDonHangDTO> daXacNhan(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDaXacNhan(id,idNhanVien));
    }

    @PutMapping("/cap-nhat-trang-thai/dang-giao-hang/{id}")
    public ResponseEntity<ListDonHangDTO> dangGiaoHang(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDangGiaoHang(id,idNhanVien));
    }
    @PutMapping("/cap-nhat-trang-thai/da-giao-hang/{id}")
    public ResponseEntity<ListDonHangDTO> daGiaoHang(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiDaGiaoHang(id,idNhanVien));
    }
    @PutMapping("/cap-nhat-trang-thai/nhan-vien-huy/{id}")
    public ResponseEntity<ListDonHangDTO> nhanVienHuy(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiNhanVienHuy(id,idNhanVien));
    }
    @PutMapping("/cap-nhat-trang-thai/giao-hang-that-bai/{id}")
    public ResponseEntity<ListDonHangDTO> giaoHangThatBai(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiGHThatBai(id,idNhanVien));
    }

}
