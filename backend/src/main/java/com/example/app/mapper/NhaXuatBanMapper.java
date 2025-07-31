package com.example.app.mapper;

import com.example.app.dto.nhaXuatBanDTO.NhaXuatBanDTO;
import com.example.app.entity.NhaXuatBan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NhaXuatBanMapper {
    NhaXuatBanDTO toDTO(NhaXuatBan entity);
    NhaXuatBan toEntity(NhaXuatBanDTO dto);
}