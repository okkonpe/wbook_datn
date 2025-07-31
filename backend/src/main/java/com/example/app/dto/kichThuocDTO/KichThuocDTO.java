package com.example.app.dto.kichThuocDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KichThuocDTO {
    private Integer id;
    private String maKichThuoc;
    private String chiSoKichThuoc;
    private Boolean trangThai;
}