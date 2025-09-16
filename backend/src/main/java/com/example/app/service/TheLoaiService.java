package com.example.app.service;

import com.example.app.dto.tacGiaDTO.TacGiaDTO;
import com.example.app.dto.theLoaiDTO.TheLoaiDTO;
import com.example.app.entity.TheLoai;
import com.example.app.mapper.QLSachMapper.TheLoaiMapper;
import com.example.app.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheLoaiService {
    @Autowired
    private TheLoaiRepository theLoaiRepository;
    @Autowired
    private TheLoaiMapper theLoaiMapper;

    public Page<TheLoaiDTO> getAll(Pageable pageable) {
        Page<TheLoaiDTO> dto = theLoaiRepository.findAll(pageable).map(theLoaiMapper::toDTO);
        System.out.println(dto);
        return dto;
    }

    public TheLoaiDTO save(TheLoaiDTO theLoaiDTO) {
        String generatedMa = generateUniqueMaTheLoai();
        theLoaiDTO.setMaTheLoai(generatedMa);
        theLoaiRepository.save(theLoaiMapper.toEntity(theLoaiDTO));
        return theLoaiDTO;
    }

    public TheLoaiDTO update(TheLoaiDTO theLoaiDTO, Integer id) {
        TheLoai entity = theLoaiRepository.findById(id).orElseThrow();
        entity.setTenTheLoai(theLoaiDTO.getTenTheLoai());
        return theLoaiMapper.toDTO(theLoaiRepository.save(entity));
    }

    public void delete(Integer id) {
        TheLoai entity = theLoaiRepository.findById(id).orElseThrow();
        theLoaiRepository.delete(entity);
    }
    private String generateUniqueMaTheLoai() {
        String prefix = "TL";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // TL001, TL002,...
            number++;
        } while (theLoaiRepository.existsByMaTheLoai(ma)); // Check trùng

        return ma;
    }
}