package com.example.app.dto.sanPhamDTO;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private Integer id;
    private String maSanPham;
    private String tenSanPham;
    private Date ngayTao;
    private Integer phienBan;
    private String moTa;
    private Boolean trangThai;
}