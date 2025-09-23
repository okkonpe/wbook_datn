package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String maVoucher;

    private String moTa;

    @Enumerated(EnumType.STRING)
    private LoaiGiam loaiGiam; // PERCENT, AMOUNT

    private BigDecimal giaTri;
    private BigDecimal giamToiDa;
    private BigDecimal donToiThieu;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;


    private Boolean trangThai = true;
}
