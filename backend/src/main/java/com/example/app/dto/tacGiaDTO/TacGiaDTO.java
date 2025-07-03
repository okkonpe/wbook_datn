package com.example.app.dto.tacGiaDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TacGiaDTO {
    private Integer id;
    private String maTacGia;
    private String tenTacGia;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
    private String moTa;
    private Boolean trangThai;
}
