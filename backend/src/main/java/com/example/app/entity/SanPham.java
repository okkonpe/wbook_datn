package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_san_pham", nullable = false, length = 15)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = false, length = 30)
    private String tenSanPham;

    @Column(name = "ngay_tao")
    @Temporal(TemporalType.DATE)
    private Date ngayTao;

    @Column(name = "mo_ta", length = 150)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "phien_ban")
    private Integer phienBan;
}

