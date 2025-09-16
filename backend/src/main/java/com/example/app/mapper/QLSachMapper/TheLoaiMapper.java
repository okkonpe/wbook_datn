package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.theLoaiDTO.TheLoaiDTO;
import com.example.app.entity.TheLoai;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TheLoaiMapper {
    TheLoaiDTO toDTO(TheLoai theLoai);
    TheLoai toEntity(TheLoaiDTO theLoaiDTO);
}

