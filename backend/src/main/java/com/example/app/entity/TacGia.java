package com.example.app.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tac_gia")
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ma_tac_gia")
    private String maTacGia;
    @Column(name = "ten_tac_gia")
    private String tenTacGia;
    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "mo_ta")
    private String moTa;
    @Column(name = "trang_thai")
    private Boolean trangThai;
    @ManyToMany(mappedBy = "tacGia")
    private Set<Book> books = new HashSet<>();
}
