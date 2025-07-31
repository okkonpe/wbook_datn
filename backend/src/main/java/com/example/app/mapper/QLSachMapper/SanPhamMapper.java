package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.sanPhamSachDTO.SanPhamDTO;
import com.example.app.entity.SanPham;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SanPhamMapper {
    SanPhamDTO toDTO(SanPham sanPham);
    SanPham toEntity(SanPhamDTO sanPhamDTO);
}
