package com.example.app.repository;

import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.KhachHang;
import com.example.app.entity.TrangThaiHoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon,Integer> {
    boolean existsByMaHoaDon(String maHoaDon);
    Optional<HoaDon> findByKhachHangAndTrangThai(KhachHang kh, TrangThaiHoaDon trangThaiHoaDon);
    Optional<HoaDon> findByKhachHangIdAndTrangThaiId(Integer khachHangId, Integer trangThaiId);
    Page<HoaDon> findByTrangThaiIdNotIn(List<Integer> trangThaiId, Pageable pageable);
    @Query("""
    SELECT DISTINCT hd FROM HoaDon hd
    JOIN FETCH hd.chiTietHoaDons cthd
    JOIN FETCH cthd.book spct
    JOIN FETCH spct.sanPham sp
    LEFT JOIN FETCH spct.hinhAnh ha
    WHERE hd.khachHang = :khachHang
      AND hd.trangThai.id NOT IN :id
    ORDER BY hd.ngayTao DESC
""")       List<HoaDon> findByKhachHangAndTrangThaiIdNotInOrderByNgayTaoDesc(KhachHang khachHang,List<Integer> id);
    @Query("SELECT h FROM HoaDon h " +
            "WHERE (:status IS NULL OR h.trangThai.trangThai = :status) "+
            "and (:loaiTT IS NULL OR h.loaiThanhToan=:loaiTT)"+
            "and (:maHoaDon IS NULL OR h.maHoaDon=:maHoaDon)"
             )
    Page<HoaDon> searchHoaDon(@Param("loaiTT") String loaiTT,
                              @Param("status") String status,
                              @Param("maHoaDon") String maHoaDon,
                              Pageable pageable);

    // Tính doanh thu từ tất cả trạng thái hợp lệ (trừ chờ xác nhận, giao hàng thất bại, hủy hàng)
    @Query("SELECT COALESCE(SUM(COALESCE(h.tongTienSauGiam, h.tongTien)),0) FROM HoaDon h WHERE h.trangThai.id IN (4,16) AND h.ngayTao BETWEEN :from AND :to")
    java.math.BigDecimal sumRevenueBetween(@Param("from") java.time.LocalDate from, @Param("to") java.time.LocalDate to);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai.id IN (2,3,4,5,11,14,15,16)")
    long countAllOrders();


    // Top sản phẩm bán chạy
    @Query("SELECT b.id as id, b.maSanPhamChiTiet as maSanPhamChiTiet, " +
           "sp.tenSanPham as tenSanPham, SUM(hct.soLuongMua) as soLuongDaBan " +
           "FROM HoaDonChiTiet hct " +
           "JOIN hct.book b " +
           "JOIN b.sanPham sp " +
           "JOIN hct.hoaDon h " +
           "WHERE h.trangThai.id IN (4,16) " +
           "GROUP BY b.id, b.maSanPhamChiTiet, sp.tenSanPham " +
           "ORDER BY soLuongDaBan DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);

    // Sản phẩm sắp hết hàng
    @Query("SELECT b.id as id, b.maSanPhamChiTiet as maSanPhamChiTiet, " +
           "sp.tenSanPham as tenSanPham, b.soLuong as soLuong " +
           "FROM Book b " +
           "JOIN b.sanPham sp " +
           "WHERE b.soLuong <= 5 " +
           "ORDER BY b.soLuong ASC")
    List<Object[]> findLowStockProducts(@Param("limit") int limit);
}
