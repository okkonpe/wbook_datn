package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.chuDeDTO.ChuDeDTO;
import com.example.app.entity.ChuDe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChuDeMapper {
    ChuDeDTO toDTO(ChuDe chuDe);
    ChuDe toEntity(ChuDeDTO chuDeDTO);
}
