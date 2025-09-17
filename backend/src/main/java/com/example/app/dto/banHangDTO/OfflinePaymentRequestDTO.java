package com.example.app.dto.banHangDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfflinePaymentRequestDTO {
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam; // Tổng tiền sau khi giảm giá
    private BigDecimal giamGia;
    private BigDecimal khachThanhToan;
    private BigDecimal tienThua;
    private Long voucherId; // ID của voucher được chọn
    private List<Item> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Integer id; // book id
        private Integer soLuong;
        private BigDecimal donGia;
    }
}


