package com.example.app.controller;

import com.example.app.entity.Voucher;
import com.example.app.repository.VoucherRepo;
import com.example.app.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherRepo voucherRepository;
    // GET /api/admin/voucher
    @GetMapping
    public List<Voucher> getAll() {
        return voucherService.getAllVouchers();
    }
    @GetMapping("/apply")
    public List<Voucher> getValidVouchers(
            @RequestParam BigDecimal tongTien
            ) {
        LocalDate today = LocalDate.now();
        return voucherRepository.findAll().stream()
                .filter(v -> v.getTrangThai())
                .filter(v -> v.getNgayBatDau().compareTo(today) <= 0
                        && v.getNgayKetThuc().compareTo(today) >= 0)
                .filter(v -> tongTien.compareTo(v.getDonToiThieu()) >= 0)
                .toList();
    }

    // POST /api/admin/voucher
    @PostMapping
    public Voucher create(@RequestBody Voucher voucher) {
        return voucherService.createVoucher(voucher);
    }

    // PUT /api/admin/voucher/{id}
    @PutMapping("/{id}")
    public Voucher update(@PathVariable Long id, @RequestBody Voucher voucher) {
        return voucherService.updateVoucher(id, voucher);
    }

    // DELETE /api/admin/voucher/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok().body("Xóa voucher thành công");
    }

    // GET /api/admin/voucher/{id}
    @GetMapping("/{id}")
    public Voucher getById(@PathVariable Long id) {
        return voucherService.getVoucherById(id);
    }

}
