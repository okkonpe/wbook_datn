package com.example.app.mapper.banHangMapper;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.NhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",uses = GioHangMapper.class)
public interface HoaDonMapper {
    HoaDonRequestDTO hoaDonReqtoDTO(HoaDon hoaDon);
    HoaDon hoaDonReqtoEntity(HoaDonRequestDTO dto);
    @Mapping(source = "khachHang.tenKhachHang", target = "khachHang")
    @Mapping(source = "nhanVien", target = "nhanVien")
    @Mapping(source = "trangThai.id", target = "trangThaiID")
    @Mapping(source = "trangThai.trangThai", target = "trangThai")
    @Mapping(source = "chiTietHoaDons", target = "sanPhams")
    @Mapping(target = "idNhanVien", expression = "java(getNhanVienId(hoaDon))")
    @Mapping(target = "idShipper", expression = "java(getShipperId(hoaDon))")
    ListDonHangDTO donHangtoDTO(HoaDon hoaDon);
//    HoaDon donHangtoEntity();
default List<String> mapNhanVienSetToTenList(Set<NhanVien> nhanVien) {
    if (nhanVien == null) return new ArrayList<>();
    return nhanVien.stream()
            .map(NhanVien::getTenNv)
            .collect(Collectors.toList());
}
    default Integer getNhanVienId(HoaDon hoaDon) {
        return hoaDon.getNhanVien().stream()
                .filter(nv -> nv.getChucVu() != null && ("ROLE_NHAN_VIEN".equalsIgnoreCase(nv.getChucVu().getTenChucVu())||"ROLE_ADMIN".equalsIgnoreCase(nv.getChucVu().getTenChucVu())))
                .map(NhanVien::getId)
                .findFirst()
                .orElse(null);
    }

    default Integer getShipperId(HoaDon hoaDon) {
        return hoaDon.getNhanVien().stream()
                .filter(nv -> nv.getChucVu() != null && "ROLE_SHIPPER".equalsIgnoreCase(nv.getChucVu().getTenChucVu()))
                .map(NhanVien::getId)
                .findFirst()
                .orElse(null);
    }

}
