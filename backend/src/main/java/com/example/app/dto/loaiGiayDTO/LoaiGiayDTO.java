package com.example.app.dto.loaiGiayDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoaiGiayDTO {

    private Integer id;
    private String maGiay;
    private String tenGiay;
    private String mauSac;
    private Boolean trangThai;
}