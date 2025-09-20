package com.example.app.mapper;

import com.example.app.dto.TaiBanDTO;
import com.example.app.entity.TaiBan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaiBanMapper {
    
    TaiBanMapper INSTANCE = Mappers.getMapper(TaiBanMapper.class);
    
    TaiBanDTO toDTO(TaiBan taiBan);
    
    TaiBan toEntity(TaiBanDTO taiBanDTO);
    
    List<TaiBanDTO> toDTOList(List<TaiBan> taiBans);
    
    List<TaiBan> toEntityList(List<TaiBanDTO> taiBanDTOs);
}
