package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chuc_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChucVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ma_chuc_vu", length = 15)
    private String maChucVu;

    @Column(name = "ten_chuc_vu", length = 50)
    private String tenChucVu;
} 