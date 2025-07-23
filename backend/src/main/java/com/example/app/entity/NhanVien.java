package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ma_nv", length = 15)
    private String maNv;

    @Column(name = "ten_nv", length = 30)
    private String tenNv;

    @Column(name = "luong")
    private BigDecimal luong;

    @Column(name = "sdt", length = 13)
    private String sdt;

    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "dia_chi", length = 50)
    private String diaChi;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "cccd", length = 13, nullable = false)
    private String cccd;

    @Column(name = "mat_khau", length = 20)
    private String matKhau;

    @Column(name = "tai_khoan", length = 20)
    private String taiKhoan;

    @Column(name = "ngay_bat_dau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @ManyToOne
    @JoinColumn(name = "chuc_vu", referencedColumnName = "id")
    private ChucVu chucVu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
} 

