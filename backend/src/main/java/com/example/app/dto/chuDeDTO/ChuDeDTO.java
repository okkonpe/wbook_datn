package com.example.app.dto.chuDeDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuDeDTO {
    private Integer id;
    private String maChuDe;
    private String tenChuDe;
    private String moTa;
    private Boolean trangThai;
}
