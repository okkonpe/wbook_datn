package com.example.app.dto.banHangDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ListGioHangDTO {
    private Integer idSanPham;
   private String hinhAnh;
   private String tenSanPham;
   private BigDecimal donGia;
    private BigDecimal tongTien;
    private Integer soLuongMua;

    public ListGioHangDTO(Integer idSanPham,String tenSanPham, BigDecimal donGia, Integer soLuongMua, BigDecimal tongTien, String hinhAnh) {
        this.idSanPham=idSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.soLuongMua = soLuongMua;
        this.tongTien = tongTien;
        this.hinhAnh = hinhAnh;
    }
}
