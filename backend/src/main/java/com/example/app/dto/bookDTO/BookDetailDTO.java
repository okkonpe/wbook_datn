package com.example.app.dto.bookDTO;

import com.example.app.dto.TaiBanDTO;
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
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailDTO {
    private Integer id;
    private String tenSanPham;
    private String isbn;
    private String maSanPhamChiTiet;
    private String theLoai;
    private Integer soTrang;
    private String nhaXuatBan;
    private String kichThuoc;
    private String loaiBia;
    private String loaiGiay;
    private Float khoiLuongTinh;
    private Integer soLuong;
    private LocalDate ngayXuatBan;
    private String hinhAnh;
    private BigDecimal donGia;
    private String moTa;
    private Boolean trangThai;
    private List<String> tacGia;
    private List<Integer> tacGiaIds;
    private List<String> chuDe;
    private List<Integer> chuDeIds;
    private Set<TaiBanDTO> taiBans;
}
