package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.nhaXuatBanDTO.NhaXuatBanDTO;
import com.example.app.entity.NhaXuatBan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NhaXuatBanMapper {
    NhaXuatBanDTO toDTO(NhaXuatBan nhaXuatBan);
    NhaXuatBan toEntity(NhaXuatBanDTO nhaXuatBanDTO);
}

