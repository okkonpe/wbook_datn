package com.example.app.mapper;

import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.entity.KhachHang;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KhachHangMapper {
    KhachHangRegisterDTO khRegistertoDTO(KhachHang khachHang);
    KhachHang khRegistertoEntity(KhachHangRegisterDTO dto);
    KhachHangInfoDTO khInfoToDTO(KhachHang khachHang);
}
