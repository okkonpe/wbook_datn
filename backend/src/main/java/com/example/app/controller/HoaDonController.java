package com.example.app.controller;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.NhanVien;
import com.example.app.repository.HoaDonRepository;
import com.example.app.repository.NhanVienRepository;
import com.example.app.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @GetMapping("/don-hang")
    public ResponseEntity<Page<ListDonHangDTO>> getAlldonHang( @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ListDonHangDTO> result = hoaDonService.getDonHang(pageable);
        return ResponseEntity.ok(result);    }
    @GetMapping("/giao-hang")
    public ResponseEntity<Page<ListDonHangDTO>> getAllGiaoHang( @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ListDonHangDTO> result = hoaDonService.getGiaoHang(pageable);
        return ResponseEntity.ok(result);    }
    @PutMapping("/shipper/nhan-don/{idHoaDon}")
    public ResponseEntity<?> nhanDon(@PathVariable Integer idHoaDon, @RequestBody Map<String, Integer> data) {
        Integer idShipper = data.get("idShipper");
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElseThrow();

        // Nếu đơn đã được nhận rồ  i thì không cho nhận tiếp
//        if (hoaDon.getShipper() != null) {
//            return ResponseEntity.badRequest().body("Đơn hàng đã được nhận");
//        }

        NhanVien shipper = nhanVienRepository.findById(idShipper).orElseThrow();
      hoaDon.getNhanVien().add(shipper);
      hoaDonRepository.save(hoaDon);

        return ResponseEntity.ok().build();
    }

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
    @PutMapping("/cap-nhat-trang-thai/giao-hang/{id}")
    public ResponseEntity<ListDonHangDTO> giaoHang(@PathVariable Integer id){

        return ResponseEntity.ok(hoaDonService.chuyenTrangThaiGiaoHang(id));
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
