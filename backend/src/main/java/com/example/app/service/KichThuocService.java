package com.example.app.service;

import com.example.app.dto.kichThuocDTO.KichThuocDTO;
import com.example.app.entity.KichThuoc;
import com.example.app.mapper.QLSachMapper.KichThuocMapper;
import com.example.app.repository.KichThuocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KichThuocService {
    @Autowired
    private KichThuocRepository kichThuocRepository;
    @Autowired
    private KichThuocMapper kichThuocMapper;

    public Page<KichThuocDTO> getAll(Pageable pageable) {
        Page<KichThuocDTO> dto = kichThuocRepository.findAll(pageable).map(kichThuocMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public KichThuocDTO save(KichThuocDTO kichThuocDTO) {
        String generatedMa = generateUniqueMaKichThuoc();
        kichThuocDTO.setMaKichThuoc(generatedMa);
        kichThuocRepository.save(kichThuocMapper.toEntity(kichThuocDTO));
        return kichThuocDTO;
    }

    public KichThuocDTO update(KichThuocDTO kichThuocDTO, Integer id) {
        KichThuoc entity = kichThuocRepository.findById(id).orElseThrow();
        entity.setChiSoKichThuoc(kichThuocDTO.getChiSoKichThuoc());
        entity.setTrangThai(kichThuocDTO.getTrangThai());
        return kichThuocMapper.toDTO(kichThuocRepository.save(entity));
    }

    public void delete(Integer id) {
        KichThuoc entity = kichThuocRepository.findById(id).orElseThrow();
        kichThuocRepository.delete(entity);
    }

    private String generateUniqueMaKichThuoc() {
        String prefix = "KT";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // KT001, KT002,...
            number++;
        } while (kichThuocRepository.existsByMaKichThuoc(ma)); // Check trùng

        return ma;
    }
}
