package com.example.app.dto.kichThuocDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KichThuocDTO {
    private Integer id;
    private String maKichThuoc;
    private String chiSoKichThuoc;
    private Boolean trangThai;
}

