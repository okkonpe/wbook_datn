package com.example.app.dto.sanPhamDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSanPhamDTO {
    private Integer id;
    private String maSanPham;
    private String tenSanPham;
}
