package com.example.app.repository;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.entity.Book;
import com.example.app.entity.HoaDon;
import com.example.app.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet,Integer> {
    Optional<HoaDonChiTiet> findByHoaDonAndAndBook(HoaDon hoaDon, Book book);
    @Query("""
    SELECT new com.example.app.dto.banHangDTO.ListGioHangDTO(
        b.id,
        sp.tenSanPham,
        b.donGia,
        ct.soLuongMua,
        ct.tongTien,
          CONCAT('http://localhost:8080/uploads/', ha.hinhAnh)
    )
    FROM HoaDonChiTiet ct
    JOIN ct.book b
    JOIN b.sanPham sp
    JOIN b.hinhAnh ha
    WHERE ct.hoaDon = :hoaDon
""")
    List<ListGioHangDTO> getGioHangDTOByHoaDon(@Param("hoaDon") HoaDon hoaDon);
    List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon);
    void deleteByHoaDonAndBook(HoaDon hoaDon, Book book);

}
