package com.example.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trang_thai_hoa_don")
public class TrangThaiHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "trang_thai")
    private String trangThai;
}
