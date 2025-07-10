package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Generated;

@Entity
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
