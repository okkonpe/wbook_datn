package com.example.app.dto.banHangDTO;

import com.example.app.entity.TacGia;
import com.example.app.entity.TaiBan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListGioHangDTO {
    private Integer idSanPham;
    private String hinhAnh;
    private String tenSanPham;
    private BigDecimal donGia;
    private BigDecimal tongTien;
    private Integer soLuongMua;
    
    // Thêm các thuộc tính chi tiết của biến thể
    private String theLoai;
    private String nhaXuatBan;
    private String loaiBia;
    private String loaiGiay;
    private List<String> tacGia;
    private Set<String> taiBan;
    private String moTa;

    public ListGioHangDTO(Integer idSanPham, String tenSanPham, BigDecimal donGia, Integer soLuongMua, BigDecimal tongTien, String hinhAnh,
                          String theLoai,
             String nhaXuatBan,
             String loaiBia,
             String loaiGiay,
                          Integer lanTaiBan, // đổi thành Integer
             String moTa) {
        this.idSanPham = idSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.soLuongMua = soLuongMua;
        this.tongTien = tongTien;
        this.hinhAnh = hinhAnh;

        this.theLoai = theLoai;
        this.nhaXuatBan = nhaXuatBan;
        this.loaiBia = loaiBia;
        this.loaiGiay = loaiGiay;
        this.taiBan = Set.of(lanTaiBan+""); // wrap thành set nếu cần
        this.moTa = moTa;

    }
}
