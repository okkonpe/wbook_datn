package com.example.app.dto.khachHangDTO;

import jakarta.persistence.Column;
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
    private String tenKhachHang;
    private String sdt;
    private LocalDate ngaySinh;
    private String diaChi;
    private String email;
}
