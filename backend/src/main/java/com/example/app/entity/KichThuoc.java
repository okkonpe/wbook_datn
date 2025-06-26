package com.example.app.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_kich_thuoc", nullable = false, length = 15)
    private String maKichThuoc;

    @Column(name = "chi_so_kich_thuoc", nullable = false, length = 10)
    private String chiSoKichThuoc;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

