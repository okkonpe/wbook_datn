package com.example.app.dto.banHangDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonRequestDTO {
    private  Integer khachHangID;
    private String hoTen;
    private String diaChi;
    private String soDienThoai;
    private String ghiChu;
    private String phuongThucThanhToan;
}
