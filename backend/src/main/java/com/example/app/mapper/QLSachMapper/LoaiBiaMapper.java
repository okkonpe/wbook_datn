package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.loaiBiaDTO.LoaiBiaDTO;
import com.example.app.entity.LoaiBia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoaiBiaMapper {
    LoaiBiaDTO toDTO(LoaiBia loaiBia);
    LoaiBia toEntity(LoaiBiaDTO loaiBiaDTO);
}
