package com.example.app.dto.sanPhamSachDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamDTO {
    private Integer id;
    private String maSanPham;
    private String tenSanPham;
    private Date ngayTao;
    private String moTa;
    private Boolean trangThai;
}
