package com.example.app.mapper;

import com.example.app.dto.sanPhamDTO.SanPhamDTO;
import com.example.app.entity.SanPham;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SanPhamMapper {
    SanPhamDTO toDTO(SanPham entity);
    SanPham toEntity(SanPhamDTO dto);
}