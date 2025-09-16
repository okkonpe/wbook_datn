package com.example.app.dto.theLoaiDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TheLoaiDTO {
    private Integer id;
    private String maTheLoai;
    private String tenTheLoai;
    private Boolean trangThai;
}
