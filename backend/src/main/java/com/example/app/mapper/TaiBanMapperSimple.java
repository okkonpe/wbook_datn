package com.example.app.mapper;

import com.example.app.dto.TaiBanDTO;
import com.example.app.entity.TaiBan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaiBanMapperSimple {
    
    public TaiBanDTO toDTO(TaiBan taiBan) {
        if (taiBan == null) {
            return null;
        }
        
        return TaiBanDTO.builder()
                .id(taiBan.getId())
                .lanTaiBan(taiBan.getLanTaiBan())
                .namTaiBan(taiBan.getNamTaiBan())
                .build();
    }
    
    public TaiBan toEntity(TaiBanDTO taiBanDTO) {
        if (taiBanDTO == null) {
            return null;
        }
        
        return TaiBan.builder()
                .id(taiBanDTO.getId())
                .lanTaiBan(taiBanDTO.getLanTaiBan())
                .namTaiBan(taiBanDTO.getNamTaiBan())
                .build();
    }
    
    public List<TaiBanDTO> toDTOList(List<TaiBan> taiBans) {
        if (taiBans == null) {
            return null;
        }
        
        return taiBans.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<TaiBan> toEntityList(List<TaiBanDTO> taiBanDTOs) {
        if (taiBanDTOs == null) {
            return null;
        }
        
        return taiBanDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
