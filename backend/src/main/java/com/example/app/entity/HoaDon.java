package com.example.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "ho_ten_nguoi_nhan")
    private String hoTenNguoiNhan;
    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;
    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;
    @Column(name = "ghi_chu")
    private String ghiChu;
    @Column(name = "ly_do_huy")
    private String lyDoHuy;
    @Column(name = "loai_thanh_toan")
    private String loaiThanhToan;

    @Column(name = "phi_ship")
    private BigDecimal phiShip;

    // 🔗 FK: nhân viên
    @ManyToOne
    @JoinColumn(name = "nhan_vien")
    private NhanVien nhanVien;

    // 🔗 FK: khách hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "khach_hang", referencedColumnName = "id")
    private KhachHang khachHang;

    // 🔗 FK: phiếu giảm giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "voucher", referencedColumnName = "id")
    private Voucher voucher;

    // 🔗 FK: trạng thái hóa đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "trang_thai", referencedColumnName = "id")
    private TrangThaiHoaDon trangThai;
    @JsonIgnoreProperties({"hoaDon", "hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> chiTietHoaDons = new ArrayList<>();

}
