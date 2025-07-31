package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.loaiGiayDTO.LoaiGiayDTO;
import com.example.app.entity.LoaiGiay;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoaiGiayMapper {
    LoaiGiayDTO toDTO(LoaiGiay loaiGiay);
    LoaiGiay toEntity(LoaiGiayDTO loaiGiayDTO);
}