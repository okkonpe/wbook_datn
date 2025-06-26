package com.example.app.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "the_loai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_the_loai", length = 15)
    private String maTheLoai;

    @Column(name = "ten_the_loai", length = 30)
    private String tenTheLoai;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

