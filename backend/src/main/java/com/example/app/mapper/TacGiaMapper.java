package com.example.app.mapper;

import com.example.app.dto.tacGiaDTO.TacGiaDTO;
import com.example.app.entity.TacGia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.swing.table.TableColumn;

@Mapper(componentModel = "spring")
public interface TacGiaMapper {
 TacGiaDTO toDTO(TacGia tacGia);
 TacGia toEntity(TacGiaDTO tacGiaDTO);

}
