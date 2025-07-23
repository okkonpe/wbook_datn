package com.example.app.repository;

import com.example.app.entity.HoaDon;
import com.example.app.entity.KhachHang;
import com.example.app.entity.TrangThaiHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon,Integer> {
    boolean existsByMaHoaDon(String maHoaDon);
    Optional<HoaDon> findByKhachHangAndTrangThai(KhachHang kh, TrangThaiHoaDon trangThaiHoaDon);
    Optional<HoaDon> findByKhachHangIdAndTrangThaiId(Integer khachHangId, Integer trangThaiId);
    Page<HoaDon> findByTrangThaiIdNot(Integer trangThaiId, Pageable pageable);
    @EntityGraph(attributePaths = {"chiTietHoaDons", "chiTietHoaDons.book", "chiTietHoaDons.book.sanPham", "chiTietHoaDons.book.hinhAnh"})
    List<HoaDon> findByKhachHangAndTrangThaiIdNotInOrderByNgayTaoDesc(KhachHang khachHang,List<Integer> id);
}
