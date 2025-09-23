package com.example.app.controller;

import com.example.app.entity.Voucher;
import com.example.app.repository.VoucherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/voucher")
public class PublicVoucherController {
    
    @Autowired
    private VoucherRepo voucherRepository;

    @GetMapping("/active")
    public List<Voucher> getActiveVouchers() {
        LocalDate today = LocalDate.now();
        return voucherRepository.findAll().stream()
                .filter(v -> v.getTrangThai())
                .filter(v -> v.getNgayBatDau().compareTo(today) <= 0
                        && v.getNgayKetThuc().compareTo(today) >= 0)
                .toList();
    }
}
