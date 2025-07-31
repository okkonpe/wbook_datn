package com.example.app.mapper.QLSachMapper;

import com.example.app.dto.kichThuocDTO.KichThuocDTO;
import com.example.app.entity.KichThuoc;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KichThuocMapper {
    KichThuocDTO toDTO(KichThuoc kichThuoc);
    KichThuoc toEntity(KichThuocDTO kichThuocDTO);
}
