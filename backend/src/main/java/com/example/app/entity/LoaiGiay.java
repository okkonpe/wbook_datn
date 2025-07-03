package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loai_giay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiGiay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giay", nullable = false, length = 15)
    private String maGiay;

    @Column(name = "ten_giay", nullable = false, length = 30)
    private String tenGiay;

    @Column(name = "mau_sac", length = 15)
    private String mauSac;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

