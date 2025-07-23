package com.example.datn_tuan.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DoanhThuNgayDTO {
    private LocalDate ngay;
    private BigDecimal tongDoanhThu;
}
