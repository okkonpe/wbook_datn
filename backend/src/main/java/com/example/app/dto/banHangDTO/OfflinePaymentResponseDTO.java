package com.example.app.dto.banHangDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfflinePaymentResponseDTO {
    private Integer hoaDonId;
    private String maHoaDon;
    private BigDecimal tongTien;
    private BigDecimal khachThanhToan;
    private BigDecimal tienThua;
}


