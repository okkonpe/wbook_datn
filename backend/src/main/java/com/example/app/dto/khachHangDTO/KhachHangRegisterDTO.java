package com.example.app.dto.khachHangDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHangRegisterDTO {
    private String taiKhoan;
    private String matKhau;
    private String tenKhachHang;
    private String email;
    private String sdt;
    private String diaChi;
}
