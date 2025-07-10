package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoa_don", nullable = false, length = 15)
    private String maHoaDon;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam")
    private BigDecimal tongTienSauGiam;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    // 🔗 FK: nhân viên
    @ManyToOne
    @JoinColumn(name = "nhan_vien", referencedColumnName = "id")
    private NhanVien nhanVien;

    // 🔗 FK: khách hàng
    @ManyToOne
    @JoinColumn(name = "khach_hang", referencedColumnName = "id")
    private KhachHang khachHang;

    // 🔗 FK: phiếu giảm giá
    @ManyToOne
    @JoinColumn(name = "phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia phieuGiamGia;

    // 🔗 FK: trạng thái hóa đơn
    @ManyToOne
    @JoinColumn(name = "trang_thai", referencedColumnName = "id")
    private TrangThaiHoaDon trangThai;
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> chiTietHoaDons = new ArrayList<>();

}
