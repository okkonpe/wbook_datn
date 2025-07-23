package com.example.datn_tuan.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SachBanChayDTO {
    private String tenSanPham;
    private Long tongSoLuong;
    private BigDecimal giaBan;
}
