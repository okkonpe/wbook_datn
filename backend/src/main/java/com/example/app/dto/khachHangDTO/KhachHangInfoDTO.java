package com.example.app.dto.khachHangDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhachHangInfoDTO {
    private Integer id;           // 👈 THÊM ID
    private String tenKhachHang;
    private String sdt;
    private LocalDate ngaySinh;
    private String diaChi;
    private String email;
    private Boolean gioiTinh;     // 👈 THÊM GIỚI TÍNH
//    private String trangThai;     // 👈 THÊM TRẠNG THÁI (String)
}
