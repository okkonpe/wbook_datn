package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.chuDeDTO.ChuDeDTO;
import com.example.app.entity.ChuDe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChuDeMapper {
    ChuDeDTO toDTO(ChuDe chuDe);
    
    @Mapping(target = "books", ignore = true)
    ChuDe toEntity(ChuDeDTO chuDeDTO);
}
