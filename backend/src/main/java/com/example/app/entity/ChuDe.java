package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "chu_de")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuDe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_chu_de", length = 15)
    private String maChuDe;

    @Column(name = "ten_chu_de", length = 30)
    private String tenChuDe;
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
    @ManyToMany(mappedBy = "chuDes")
    private Set<Book> books;

}
