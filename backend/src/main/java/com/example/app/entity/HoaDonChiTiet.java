package com.example.app.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_chi_tiet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK đến hóa đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hoa_don", nullable = false)
    private HoaDon hoaDon;

    // FK đến sản phẩm chi tiết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_san_pham", nullable = false)
    private Book book;  // hoặc `SanPhamChiTiet` nếu bạn dùng tên khác

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "so_luong_mua")
    private Integer soLuongMua;

    @Column(name = "ma_hoa_don_chi_tiet", length = 15)
    private String maHoaDonChiTiet;

    @Column(name = "mo_ta", length = 100)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
