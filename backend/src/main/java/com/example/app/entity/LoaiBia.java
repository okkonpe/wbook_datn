package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loai_bia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoaiBia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_bia", nullable = false, length = 15)
    private String maBia;

    @Column(name = "ten_bia", nullable = false, length = 30)
    private String tenBia;

    @Column(name = "mau_sac", length = 15)
    private String mauSac;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

