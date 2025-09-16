package com.example.app.service;

import com.example.app.dto.sanPhamSachDTO.SanPhamDTO;
import com.example.app.entity.SanPham;
import com.example.app.mapper.QLSachMapper.SanPhamMapper;
import com.example.app.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private SanPhamMapper sanPhamMapper;

    public Page<SanPhamDTO> getAll(Pageable pageable) {
        Page<SanPhamDTO> dto = sanPhamRepository.findAll(pageable).map(sanPhamMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public SanPhamDTO save(SanPhamDTO sanPhamDTO) {
        String generatedMa = generateUniqueMaSanPham();
        sanPhamDTO.setMaSanPham(generatedMa);
        sanPhamDTO.setNgayTao(new Date()); // Set ngày tạo hiện tại
        sanPhamRepository.save(sanPhamMapper.toEntity(sanPhamDTO));
        return sanPhamDTO;
    }

    public SanPhamDTO update(SanPhamDTO sanPhamDTO, Integer id) {
        SanPham entity = sanPhamRepository.findById(id).orElseThrow();
        entity.setTenSanPham(sanPhamDTO.getTenSanPham());
        entity.setNgayTao(sanPhamDTO.getNgayTao());
        entity.setMoTa(sanPhamDTO.getMoTa());
        entity.setTrangThai(sanPhamDTO.getTrangThai());
        return sanPhamMapper.toDTO(sanPhamRepository.save(entity));
    }

    public void delete(Integer id) {
        SanPham entity = sanPhamRepository.findById(id).orElseThrow();
        sanPhamRepository.delete(entity);
    }

    private String generateUniqueMaSanPham() {
        String prefix = "SP";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // SP001, SP002,...
            number++;
        } while (sanPhamRepository.existsByMaSanPham(ma)); // Check trùng

        return ma;
    }
}
