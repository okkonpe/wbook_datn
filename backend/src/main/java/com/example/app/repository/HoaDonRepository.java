package com.example.app.repository;

import com.example.app.entity.HoaDon;
import com.example.app.entity.KhachHang;
import com.example.app.entity.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon,Integer> {
    boolean existsByMaHoaDon(String maHoaDon);
    Optional<HoaDon> findByKhachHangAndTrangThai(KhachHang kh, TrangThaiHoaDon trangThaiHoaDon);
}
