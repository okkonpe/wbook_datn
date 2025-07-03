package com.example.app.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "nha_xuat_ban")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhaXuatBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_nha_xuat_ban", nullable = false, length = 15)
    private String maNhaXuatBan;

    @Column(name = "ten_nha_xuat_ban", nullable = false, length = 30)
    private String tenNhaXuatBan;

    @Column(name = "ngay_thanh_lap")
    private LocalDate ngayThanhLap;

    @Column(name = "tru_so_chinh", length = 30)
    private String truSoChinh;

    @Column(name = "mo_ta", length = 100)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

