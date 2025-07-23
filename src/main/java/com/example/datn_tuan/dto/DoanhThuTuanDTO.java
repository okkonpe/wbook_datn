package com.example.datn_tuan.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DoanhThuTuanDTO {
    private Integer soTuan;
    private Integer nam;
    private BigDecimal tongDoanhThu;
}
