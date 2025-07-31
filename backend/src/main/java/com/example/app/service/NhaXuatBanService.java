package com.example.app.service;

import com.example.app.dto.nhaXuatBanDTO.NhaXuatBanDTO;
import com.example.app.entity.NhaXuatBan;
import com.example.app.mapper.NhaXuatBanMapper;
import com.example.app.repository.NhaXuatBanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NhaXuatBanService {
    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;
    @Autowired
    private NhaXuatBanMapper nhaXuatBanMapper;

    public Page<NhaXuatBanDTO> getAll(Pageable pageable) {
        return nhaXuatBanRepository.findAll(pageable).map(nhaXuatBanMapper::toDTO);
    }

    public NhaXuatBanDTO save(NhaXuatBanDTO nhaXuatBanDTO) {
        String generatedMa = generateUniqueMaNhaXuatBan();
        nhaXuatBanDTO.setMaNhaXuatBan(generatedMa);
        nhaXuatBanRepository.save(nhaXuatBanMapper.toEntity(nhaXuatBanDTO));
        return nhaXuatBanDTO;
    }

    public NhaXuatBanDTO update(NhaXuatBanDTO nhaXuatBanDTO, Integer id) {
        NhaXuatBan entity = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhà xuất bản không tồn tại"));
        entity.setTenNhaXuatBan(nhaXuatBanDTO.getTenNhaXuatBan());
        entity.setNgayThanhLap(nhaXuatBanDTO.getNgayThanhLap());
        entity.setTruSoChinh(nhaXuatBanDTO.getTruSoChinh());
        entity.setMoTa(nhaXuatBanDTO.getMoTa());
        entity.setTrangThai(nhaXuatBanDTO.getTrangThai());
        return nhaXuatBanMapper.toDTO(nhaXuatBanRepository.save(entity));
    }

    public void delete(Integer id) {
        NhaXuatBan entity = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhà xuất bản không tồn tại"));
        nhaXuatBanRepository.delete(entity);
    }

    private String generateUniqueMaNhaXuatBan() {
        String prefix = "NXB";
        int number = 1;
        String ma;
        do {
            ma = prefix + String.format("%03d", number); // NXB001, NXB002,...
            number++;
        } while (nhaXuatBanRepository.existsByMaNhaXuatBan(ma));
        return ma;
    }
}