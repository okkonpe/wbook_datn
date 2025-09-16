package com.example.app.service;

import com.example.app.dto.chuDeDTO.ChuDeDTO;
import com.example.app.entity.ChuDe;
import com.example.app.mapper.QLSachMapper.ChuDeMapper;
import com.example.app.repository.ChuDeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChuDeService {
    @Autowired
    private ChuDeRepository chuDeRepository;
    @Autowired
    private ChuDeMapper chuDeMapper;

    public Page<ChuDeDTO> getAll(Pageable pageable) {
        Page<ChuDeDTO> dto = chuDeRepository.findAll(pageable).map(chuDeMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public ChuDeDTO save(ChuDeDTO chuDeDTO) {
        String generatedMa = generateUniqueMaChuDe();
        chuDeDTO.setMaChuDe(generatedMa);
        chuDeRepository.save(chuDeMapper.toEntity(chuDeDTO));
        return chuDeDTO;
    }

    public ChuDeDTO update(ChuDeDTO chuDeDTO, Integer id) {
        ChuDe entity = chuDeRepository.findById(id).orElseThrow();
        entity.setTenChuDe(chuDeDTO.getTenChuDe());
        entity.setMoTa(chuDeDTO.getMoTa());
        entity.setTrangThai(chuDeDTO.getTrangThai());
        return chuDeMapper.toDTO(chuDeRepository.save(entity));
    }

    public void delete(Integer id) {
        ChuDe entity = chuDeRepository.findById(id).orElseThrow();
        chuDeRepository.delete(entity);
    }

    private String generateUniqueMaChuDe() {
        String prefix = "CD";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // CD001, CD002,...
            number++;
        } while (chuDeRepository.existsByMaChuDe(ma)); // Check trùng

        return ma;
    }
}
