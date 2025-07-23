package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ma_khach_hang")
    private String maKhachHang;
    @Column(name = "ten_khach_hang")
    private String tenKhachHang;
    @Column(name = "sdt")
    private String sdt;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "dia_chi")
   private String diaChi;
    @Column(name = "email")
    private String email;
    @Column(name = "mat_khau")
       private String matKhau;
    @Column(name = "tai_khoan")
   private String taiKhoan;
    @Column(name = "gioi_tinh")
      private Boolean gioiTinh;
    @Column(name = "trang_thai")
    private Boolean trangThai;
}
