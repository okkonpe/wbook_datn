package com.example.app.service;

import com.example.app.dto.tacGiaDTO.TacGiaDTO;
import com.example.app.entity.TacGia;
import com.example.app.mapper.TacGiaMapper;
import com.example.app.repository.TacGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class TacGiaService {
    @Autowired
    private TacGiaRepository tacGiaRepository;
    @Autowired
    private TacGiaMapper tacGiaMapper;
    public Page<TacGiaDTO> getAll(Pageable pageable){
         Page<TacGiaDTO> dto=tacGiaRepository.findAll(pageable).map(tacGiaMapper::toDTO);
        System.out.println(dto);
        return dto;
    }
    public TacGiaDTO save(TacGiaDTO tacGiaDTO){
        String generatedMa = generateUniqueMaTacGia();
        tacGiaDTO.setMaTacGia(generatedMa);
        tacGiaRepository.save(tacGiaMapper.toEntity(tacGiaDTO));
        return tacGiaDTO;
    }
    public  TacGiaDTO update(TacGiaDTO tacGiaDTO,Integer id){
        TacGia entity = tacGiaRepository.findById(id).orElseThrow();
        entity.setTenTacGia(tacGiaDTO.getTenTacGia());
        entity.setGioiTinh(tacGiaDTO.getGioiTinh());
        entity.setNgaySinh(tacGiaDTO.getNgaySinh());
        entity.setMoTa(tacGiaDTO.getMoTa());
        return tacGiaMapper.toDTO(tacGiaRepository.save(entity));
    }
    public void delete(Integer id){
        TacGia entity = tacGiaRepository.findById(id).orElseThrow();
tacGiaRepository.delete(entity);
    }
    private String generateUniqueMaTacGia() {
        String prefix = "TG";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // TG001, TG002,...
            number++;
        } while (tacGiaRepository.existsByMaTacGia(ma)); // Check trùng

        return ma;
    }


}
