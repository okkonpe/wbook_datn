package com.example.app.service;

import com.example.app.entity.LoaiGiam;
import com.example.app.entity.Voucher;
import com.example.app.repository.VoucherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepo voucherRepository;

    public Voucher createVoucher(Voucher voucher) {
        // Check mã voucher đã tồn tại
        voucherRepository.findByMaVoucher(voucher.getMaVoucher()).ifPresent(v -> {
            throw new RuntimeException("Mã voucher đã tồn tại");
        });
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Long id, Voucher updatedVoucher) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        voucher.setMaVoucher(updatedVoucher.getMaVoucher());
        voucher.setMoTa(updatedVoucher.getMoTa());
        voucher.setLoaiGiam(updatedVoucher.getLoaiGiam());
        voucher.setGiaTri(updatedVoucher.getGiaTri());
        voucher.setGiamToiDa(updatedVoucher.getGiamToiDa());
        voucher.setDonToiThieu(updatedVoucher.getDonToiThieu());
        voucher.setNgayBatDau(updatedVoucher.getNgayBatDau());
        voucher.setNgayKetThuc(updatedVoucher.getNgayKetThuc());
        voucher.setSoLuong(updatedVoucher.getSoLuong());
        voucher.setTrangThai(updatedVoucher.getTrangThai());
        voucher.setOnlyNewCustomer(updatedVoucher.getOnlyNewCustomer());

        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(Long id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Voucher không tồn tại");
        }
        voucherRepository.deleteById(id);
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
    }

    public BigDecimal applyVoucher(String maVoucher, BigDecimal tongDonHang, boolean isNewCustomer) {
        Voucher voucher = voucherRepository.findByMaVoucher(maVoucher)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        // Check điều kiện
        if (!voucher.getTrangThai()) throw new RuntimeException("Voucher không khả dụng");
        if (LocalDate.now().isBefore(voucher.getNgayBatDau()) ||
                LocalDate.now().isAfter(voucher.getNgayKetThuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }
        if (voucher.getSoLuong() <= voucher.getDaDung()) {
            throw new RuntimeException("Voucher đã hết số lượng");
        }
        if (tongDonHang.compareTo(voucher.getDonToiThieu()) < 0) {
            throw new RuntimeException("Chưa đạt đơn tối thiểu");
        }
        if (voucher.getOnlyNewCustomer() && !isNewCustomer) {
            throw new RuntimeException("Voucher chỉ áp dụng cho khách hàng mới");
        }

        // Tính giảm
        BigDecimal giam;
        if (voucher.getLoaiGiam() == LoaiGiam.PERCENT) {
            giam = tongDonHang.multiply(voucher.getGiaTri()).divide(BigDecimal.valueOf(100));
            if (voucher.getGiamToiDa() != null && giam.compareTo(voucher.getGiamToiDa()) > 0) {
                giam = voucher.getGiamToiDa();
            }
        } else {
            giam = voucher.getGiaTri();
        }

        return giam;
    }

}
