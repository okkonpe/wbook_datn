package com.example.app.dto.banHangDTO;

import com.example.app.entity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListDonHangDTO {
private Integer id;
private String maHoaDon;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private LocalDate ngayTao;
    private String hoTenNguoiNhan;
    private String diaChiGiaoHang;
    private String sdtNguoiNhan;
    private String ghiChu;
    private String lyDoHuy;
    private String loaiThanhToan;
    private List<String> nhanVien;
    private String khachHang;
//    private PhieuGiamGia phieuGiamGia;
    private String trangThai;
    private Integer trangThaiID;
    private  Integer idShipper;
    private  Integer idNhanVien;
    private List<ListGioHangDTO> sanPhams;
}
