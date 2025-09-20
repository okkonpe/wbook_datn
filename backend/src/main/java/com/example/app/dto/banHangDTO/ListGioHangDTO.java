package com.example.app.dto.banHangDTO;

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
    private String isbn;
    private String maSanPhamChiTiet;
    private String theLoai;
    private String nhaXuatBan;
    private String kichThuoc;
    private String loaiBia;
    private String loaiGiay;
    private Integer soTrang;
    private Float khoiLuongTinh;
    private LocalDate ngayXuatBan;
    private List<String> tacGia;
    private List<String> chuDe;
    private Set<String> taiBans;
    private String moTa;

    public ListGioHangDTO(Integer idSanPham, String tenSanPham, BigDecimal donGia, Integer soLuongMua, BigDecimal tongTien, String hinhAnh) {
        this.idSanPham = idSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.soLuongMua = soLuongMua;
        this.tongTien = tongTien;
        this.hinhAnh = hinhAnh;
    }
}
