package com.example.app.dto.nhaXuatBanDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhaXuatBanDTO {
    private Integer id;
    private String maNhaXuatBan;
    private String tenNhaXuatBan;
    private LocalDate ngayThanhLap;
    private String truSoChinh;
    private String moTa;
    private Boolean trangThai;
}