package com.example.app.service;

import com.example.app.dto.ChucVuDTO;
import com.example.app.entity.ChucVu;
import com.example.app.repository.ChucVuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChucVuService {

    @Autowired
    private ChucVuRepository chucVuRepository;

    public List<ChucVuDTO> getAllChucVu() {
        return chucVuRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ChucVuDTO> getAllChucVuPaged(Pageable pageable) {
        return chucVuRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public ChucVuDTO getChucVuById(Integer id) {
        return chucVuRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chức vụ với id = " + id));
    }

    public ChucVuDTO createChucVu(ChucVuDTO chucVuDTO) {
        // Kiểm tra mã chức vụ đã tồn tại chưa
        if (chucVuRepository.existsByMaChucVu(chucVuDTO.getMaChucVu())) {
            throw new IllegalArgumentException("Mã chức vụ đã tồn tại");
        }

        ChucVu chucVu = toEntity(chucVuDTO);
        ChucVu saved = chucVuRepository.save(chucVu);
        return toDTO(saved);
    }

    public ChucVuDTO updateChucVu(Integer id, ChucVuDTO chucVuDTO) {
        ChucVu chucVu = chucVuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chức vụ với id = " + id));

        // Kiểm tra mã chức vụ đã tồn tại cho các chức vụ khác chưa
        if (!chucVu.getMaChucVu().equals(chucVuDTO.getMaChucVu()) && 
                chucVuRepository.existsByMaChucVu(chucVuDTO.getMaChucVu())) {
            throw new IllegalArgumentException("Mã chức vụ đã tồn tại");
        }

        chucVu.setMaChucVu(chucVuDTO.getMaChucVu());
        chucVu.setTenChucVu(chucVuDTO.getTenChucVu());

        ChucVu updated = chucVuRepository.save(chucVu);
        return toDTO(updated);
    }

    public void deleteChucVu(Integer id) {
        ChucVu chucVu = chucVuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chức vụ với id = " + id));
        
        chucVuRepository.delete(chucVu);
    }

    // Mapper methods
    private ChucVuDTO toDTO(ChucVu entity) {
        ChucVuDTO dto = new ChucVuDTO();
        dto.setId(entity.getId());
        dto.setMaChucVu(entity.getMaChucVu());
        dto.setTenChucVu(entity.getTenChucVu());
        return dto;
    }

    private ChucVu toEntity(ChucVuDTO dto) {
        ChucVu entity = new ChucVu();
        entity.setId(dto.getId());
        entity.setMaChucVu(dto.getMaChucVu());
        entity.setTenChucVu(dto.getTenChucVu());
        return entity;
    }

    public String generateUniqueMaChucVu() {
        String prefix = "CV";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // CV001, CV002,...
            number++;
        } while (chucVuRepository.existsByMaChucVu(ma));

        return ma;
    }
} 