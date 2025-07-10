package com.example.app.service;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.dto.banHangDTO.ThemGioHangDTO;
import com.example.app.entity.*;
import com.example.app.mapper.banHangMapper.GioHangMapper;
import com.example.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    TrangThaiHoaDonRepo trangThaiHoaDonRepo;

    @Autowired
    HoaDonChiTietRepo gioHangRepo;
    @Autowired
    GioHangMapper gioHangMapper;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    KhachHangRepo khachHangRepo;
    KhachHang kh = new KhachHang();
    public HoaDon createHoaDon(KhachHang khachHang){
        HoaDon hoaDon = new HoaDon();
                hoaDon.setKhachHang(khachHang);
        //        hoaDon.setNhanVien(nhanVien);
        hoaDon.setMaHoaDon(taoMaHoaDonTuDong());
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setTrangThai(trangThaiHoaDonRepo.findByTrangThai("GIO_HANG") .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIO_HANG"))); // FK
        hoaDon = hoaDonRepository.save(hoaDon);
    return hoaDon;
}
public ThemGioHangDTO themGioHang(ThemGioHangDTO gioHangDTO){

    kh.setId(11);

    Book book =  bookRepository.findById(gioHangDTO.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
    TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIỎ_HÀNG"));
    HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
            .orElseGet(() -> {
            return    createHoaDon(kh);
            });
    Optional<HoaDonChiTiet> existingCT = gioHangRepo.findByHoaDonAndAndBook(hoaDon, book);

    if (existingCT.isPresent()) {
        HoaDonChiTiet ct = existingCT.get();
        int newSoLuong = ct.getSoLuongMua() + gioHangDTO.getSoLuong();
        if (newSoLuong <= 0) {
            gioHangRepo.delete(ct);
        } else {
            ct.setSoLuongMua(newSoLuong);
            ct.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(newSoLuong)));
            gioHangRepo.save(ct);
        }
    } else {
        HoaDonChiTiet newCT = new HoaDonChiTiet();
        newCT.setHoaDon(hoaDon);
        newCT.setBook(book);
        newCT.setSoLuongMua(gioHangDTO.getSoLuong());
        newCT.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(gioHangDTO.getSoLuong())));
        newCT.setTrangThai(true);
        gioHangRepo.save(newCT);
    }


    List<HoaDonChiTiet> chiTietList = gioHangRepo.findByHoaDon(hoaDon);

    BigDecimal tongTien = chiTietList.stream()
            .map(HoaDonChiTiet::getTongTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    int tongSoLuong = chiTietList.stream()
            .mapToInt(HoaDonChiTiet::getSoLuongMua)
            .sum();

    hoaDon.setTongTien(tongTien);

    hoaDonRepository.save(hoaDon);
return gioHangDTO;
}

    public List<ListGioHangDTO> getGioHangByKhachHangId(Integer khachHangId) {
        KhachHang kh = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIO_HANG"));

        HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
                .orElseThrow(() -> new RuntimeException("Khách hàng chưa có giỏ hàng"));

        List<ListGioHangDTO> chiTietList = gioHangRepo.getGioHangDTOByHoaDon(hoaDon);

        return chiTietList;
    }
    @Transactional
    public void xoaSanPhamKhoiGioHang(Integer khachHangId, Integer idSanPham) {
        KhachHang kh = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIỎ_HÀNG"));

        HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        Book book = bookRepository.findById(idSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        gioHangRepo.deleteByHoaDonAndBook(hoaDon, book);
    }


    public String taoMaHoaDonTuDong() {
        String prefix = "HD";
        String ngayHomNay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseMa = prefix + ngayHomNay;
        int stt = 1;
        String maHoaDon;

        do {
            String soThuTu = String.format("%04d", stt); // VD: 0001
            maHoaDon = baseMa + soThuTu;
            stt++;
        } while (hoaDonRepository.existsByMaHoaDon(maHoaDon));

        return maHoaDon;
    }

}
