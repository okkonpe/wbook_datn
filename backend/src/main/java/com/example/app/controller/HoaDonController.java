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

    @PostMapping("/offline/thanh-toan")
    public ResponseEntity<com.example.app.dto.banHangDTO.OfflinePaymentResponseDTO> thanhToanOffline(
            @RequestBody com.example.app.dto.banHangDTO.OfflinePaymentRequestDTO dto) {
        if (dto.getTienThua() != null && dto.getTienThua().signum() < 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(hoaDonService.thanhToanOffline(dto));
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

    @PutMapping("/cap-nhat-trang-thai/khach-hang-huy/{id}")
    public ResponseEntity<ListDonHangDTO> khachHangHuy(@PathVariable Integer id){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiKhachHangHuy(id));
    }
    @PutMapping("/cap-nhat-trang-thai/giao-hang-that-bai/{id}")
    public ResponseEntity<ListDonHangDTO> giaoHangThatBai(@PathVariable Integer id,@RequestParam Integer idNhanVien){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiGHThatBai(id,idNhanVien));
    }

    // ======= Dashboard stats (simple APIs) =======
    @GetMapping("/stat/summary")
    public ResponseEntity<java.util.Map<String,Object>> summary() {
        java.util.Map<String,Object> m = new java.util.HashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.math.BigDecimal todayRev = hoaDonRepository.sumRevenueBetween(today, today);
        java.math.BigDecimal weekRev = hoaDonRepository.sumRevenueBetween(today.minusDays(6), today);
        java.math.BigDecimal monthRev = hoaDonRepository.sumRevenueBetween(today.withDayOfMonth(1), today);
        m.put("todayRevenue", todayRev);
        m.put("weekRevenue", weekRev);
        m.put("monthRevenue", monthRev);
        m.put("totalOrders", hoaDonRepository.countAllOrders());
        m.put("totalRevenue", weekRev);
        m.put("activeVouchers", 0);
        m.put("topSellingCount", 0);
        m.put("lowStockCount", 0);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/stat/revenue")
    public ResponseEntity<java.util.List<java.util.Map<String,Object>>> revenue(
            @RequestParam String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate start;
        java.time.LocalDate end;
        java.util.List<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
        if ("day".equalsIgnoreCase(type)) {
            start = today; end = today;
            java.util.Map<String,Object> row = new java.util.HashMap<>();
            row.put("label", today.toString());
            row.put("value", hoaDonRepository.sumRevenueBetween(start, end));
            list.add(row);
        } else if ("week".equalsIgnoreCase(type)) {
            for (int i=6;i>=0;i--) {
                java.time.LocalDate d = today.minusDays(i);
                java.util.Map<String,Object> row = new java.util.HashMap<>();
                row.put("label", d.toString());
                row.put("value", hoaDonRepository.sumRevenueBetween(d, d));
                list.add(row);
            }
        } else if ("month".equalsIgnoreCase(type)) {
            java.time.LocalDate first = today.withDayOfMonth(1);
            java.time.LocalDate cur = first;
            while (!cur.isAfter(today)) {
                java.util.Map<String,Object> row = new java.util.HashMap<>();
                row.put("label", cur.toString());
                row.put("value", hoaDonRepository.sumRevenueBetween(cur, cur));
                list.add(row);
                cur = cur.plusDays(1);
            }
        } else {
            start = java.time.LocalDate.parse(from);
            end = java.time.LocalDate.parse(to);
            java.time.LocalDate cur = start;
            while (!cur.isAfter(end)) {
                java.util.Map<String,Object> row = new java.util.HashMap<>();
                row.put("label", cur.toString());
                row.put("value", hoaDonRepository.sumRevenueBetween(cur, cur));
                list.add(row);
                cur = cur.plusDays(1);
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/stat/top-selling")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(hoaDonService.getTopSellingProducts(limit));
    }

    @GetMapping("/stat/low-stock")
    public ResponseEntity<List<Map<String, Object>>> getLowStockProducts(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(hoaDonService.getLowStockProducts(limit));
    }
}
