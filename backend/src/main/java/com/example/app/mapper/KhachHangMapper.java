package com.example.app.mapper;

import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.entity.KhachHang;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KhachHangMapper {

    // Register mapping (giữ nguyên)
    KhachHangRegisterDTO khRegistertoDTO(KhachHang khachHang);
    KhachHang khRegistertoEntity(KhachHangRegisterDTO dto);

    // Info mapping - MapStruct tự động map tất cả trường có tên giống nhau
    KhachHangInfoDTO khInfoToDTO(KhachHang khachHang);
    KhachHang khInfoToEntity(KhachHangInfoDTO dto);  // 👈 THÊM METHOD NÀY
}
