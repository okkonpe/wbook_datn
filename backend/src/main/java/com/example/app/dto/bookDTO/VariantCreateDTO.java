package com.example.app.dto.bookDTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class VariantCreateDTO {
    private Integer sanPhamId;
    private String isbn;
    private String maSanPhamChiTiet;
    private Integer theLoaiId;
    private Integer nhaXuatBanId;
    private Integer kichThuocId;
    private Integer loaiBiaId;
    private Integer loaiGiayId;
    private Integer soTrang;
    private List<Integer> taiBanIds;
    private Float khoiLuongTinh;
    private Integer soLuong;
    private LocalDate ngayXuatBan;
    private String hinhAnh; // filename đã upload
    private BigDecimal donGia;
    private String moTa;
    private Boolean trangThai;
    private List<Integer> tacGiaIds;
    private List<Integer> chuDeIds;
}


