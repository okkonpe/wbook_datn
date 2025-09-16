package com.example.app.service;

import com.example.app.dto.loaiBiaDTO.LoaiBiaDTO;
import com.example.app.entity.LoaiBia;
import com.example.app.mapper.QLSachMapper.LoaiBiaMapper;
import com.example.app.repository.LoaiBiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoaiBiaService {
    @Autowired
    private LoaiBiaRepository loaiBiaRepository;
    @Autowired
    private LoaiBiaMapper loaiBiaMapper;

    public Page<LoaiBiaDTO> getAll(Pageable pageable) {
        Page<LoaiBiaDTO> dto = loaiBiaRepository.findAll(pageable).map(loaiBiaMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public LoaiBiaDTO save(LoaiBiaDTO loaiBiaDTO) {
        String generatedMa = generateUniqueMaLoaiBia();
        loaiBiaDTO.setMaBia(generatedMa);
        loaiBiaRepository.save(loaiBiaMapper.toEntity(loaiBiaDTO));
        return loaiBiaDTO;
    }

    public LoaiBiaDTO update(LoaiBiaDTO loaiBiaDTO, Integer id) {
        LoaiBia entity = loaiBiaRepository.findById(id).orElseThrow();
        entity.setTenBia(loaiBiaDTO.getTenBia());
        entity.setMauSac(loaiBiaDTO.getMauSac());
        entity.setTrangThai(loaiBiaDTO.getTrangThai());
        return loaiBiaMapper.toDTO(loaiBiaRepository.save(entity));
    }

    public void delete(Integer id) {
        LoaiBia entity = loaiBiaRepository.findById(id).orElseThrow();
        loaiBiaRepository.delete(entity);
    }

    private String generateUniqueMaLoaiBia() {
        String prefix = "LB";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // LB001, LB002,...
            number++;
        } while (loaiBiaRepository.existsByMaBia(ma)); // Check trùng

        return ma;
    }
}
