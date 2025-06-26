package com.example.app.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hinh_anh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HinhAnh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hinh_anh", nullable = false, length = 15)
    private String maHinhAnh;

    @Column(name = "hinh_anh", length = 50)
    private String hinhAnh;
}

