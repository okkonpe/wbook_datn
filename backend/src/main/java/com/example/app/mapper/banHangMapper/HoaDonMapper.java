package com.example.app.mapper.banHangMapper;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.entity.HoaDon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = GioHangMapper.class)
public interface HoaDonMapper {
    HoaDonRequestDTO hoaDonReqtoDTO(HoaDon hoaDon);
    HoaDon hoaDonReqtoEntity(HoaDonRequestDTO dto);
    @Mapping(source = "khachHang.tenKhachHang", target = "khachHang")
    @Mapping(source = "nhanVien.tenNv", target = "nhanVien")
    @Mapping(source = "trangThai.id", target = "trangThaiID")
    @Mapping(source = "trangThai.trangThai", target = "trangThai")
    @Mapping(source = "chiTietHoaDons", target = "sanPhams")
    ListDonHangDTO donHangtoDTO(HoaDon hoaDon);
//    HoaDon donHangtoEntity();

}
