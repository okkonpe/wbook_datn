package com.example.app.service;

import com.example.app.dto.loaiGiayDTO.LoaiGiayDTO;
import com.example.app.entity.LoaiGiay;
import com.example.app.mapper.QLSachMapper.LoaiGiayMapper;
import com.example.app.repository.LoaiGiayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoaiGiayService {
    @Autowired
    private LoaiGiayRepository loaiGiayRepository;
    @Autowired
    private LoaiGiayMapper loaiGiayMapper;

    public Page<LoaiGiayDTO> getAll(Pageable pageable) {
        Page<LoaiGiayDTO> dto = loaiGiayRepository.findAll(pageable).map(loaiGiayMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public LoaiGiayDTO save(LoaiGiayDTO loaiGiayDTO) {
        String generatedMa = generateUniqueMaLoaiGiay();
        loaiGiayDTO.setMaGiay(generatedMa);
        loaiGiayRepository.save(loaiGiayMapper.toEntity(loaiGiayDTO));
        return loaiGiayDTO;
    }

    public LoaiGiayDTO update(LoaiGiayDTO loaiGiayDTO, Integer id) {
        LoaiGiay entity = loaiGiayRepository.findById(id).orElseThrow();
        entity.setTenGiay(loaiGiayDTO.getTenGiay());
        entity.setMauSac(loaiGiayDTO.getMauSac());
        entity.setTrangThai(loaiGiayDTO.getTrangThai());
        return loaiGiayMapper.toDTO(loaiGiayRepository.save(entity));
    }

    public void delete(Integer id) {
        LoaiGiay entity = loaiGiayRepository.findById(id).orElseThrow();
        loaiGiayRepository.delete(entity);
    }

    private String generateUniqueMaLoaiGiay() {
        String prefix = "LG";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // LG001, LG002,...
            number++;
        } while (loaiGiayRepository.existsByMaGiay(ma)); // Check trùng

        return ma;
    }
}
