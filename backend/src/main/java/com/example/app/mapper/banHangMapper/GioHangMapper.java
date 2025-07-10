package com.example.app.mapper.banHangMapper;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.dto.banHangDTO.ThemGioHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.HoaDonChiTiet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GioHangMapper {
    ThemGioHangDTO toDTOThemGH(HoaDonChiTiet chiTiet);
    HoaDonChiTiet toEntityThemGH(ThemGioHangDTO gioHangDTO);
    ListGioHangDTO toDTOListGH(HoaDonChiTiet chiTiet);
}
