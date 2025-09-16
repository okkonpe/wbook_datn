package com.example.app.dto.nhaXuatBanDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhaXuatBanDTO {
    private Integer id;
    private String maNhaXuatBan;
    private String tenNhaXuatBan;
    private LocalDate ngayThanhLap;
    private String truSoChinh;
    private String moTa;
    private Boolean trangThai;
}
