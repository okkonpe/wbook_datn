package com.example.app.service;

import com.example.app.dto.TaiBanDTO;
import com.example.app.entity.TaiBan;
import com.example.app.mapper.TaiBanMapperSimple;
import com.example.app.repository.TaiBanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaiBanService {
    
    @Autowired
    private TaiBanRepository taiBanRepository;
    
    @Autowired
    private TaiBanMapperSimple taiBanMapper;
    
    public List<TaiBanDTO> getAllTaiBan() {
        System.out.println("TaiBanService.getAllTaiBan() called");
        try {
            List<TaiBan> taiBans = taiBanRepository.findAll();
            System.out.println("Found " + taiBans.size() + " taiBans");
            
            if (taiBans.isEmpty()) {
                System.out.println("No taiBans found in database");
                return new java.util.ArrayList<>();
            }
            
            List<TaiBanDTO> result = taiBanMapper.toDTOList(taiBans);
            System.out.println("Mapped to " + result.size() + " DTOs");
            return result;
        } catch (Exception e) {
            System.err.println("Error in getAllTaiBan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting taiBans: " + e.getMessage(), e);
        }
    }
    
    public TaiBanDTO getTaiBanById(Integer id) {
        Optional<TaiBan> taiBan = taiBanRepository.findById(id);
        return taiBan.map(taiBanMapper::toDTO).orElse(null);
    }
    
    public TaiBanDTO createTaiBan(TaiBanDTO taiBanDTO) {
        System.out.println("TaiBanService.createTaiBan() called with: " + taiBanDTO);
        try {
            if (taiBanRepository.existsByLanTaiBan(
                    taiBanDTO.getLanTaiBan())) {
                throw new RuntimeException("Tái bản này đã tồn tại");
            }
            
            TaiBan taiBan = taiBanMapper.toEntity(taiBanDTO);
            System.out.println("Created entity: " + taiBan);
            TaiBan savedTaiBan = taiBanRepository.save(taiBan);
            System.out.println("Saved entity: " + savedTaiBan);
            TaiBanDTO result = taiBanMapper.toDTO(savedTaiBan);
            System.out.println("Returning DTO: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error in createTaiBan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating taiBan: " + e.getMessage(), e);
        }
    }
    
    public TaiBanDTO updateTaiBan(Integer id, TaiBanDTO taiBanDTO) {
        Optional<TaiBan> existingTaiBan = taiBanRepository.findById(id);
        if (existingTaiBan.isPresent()) {
            TaiBan taiBan = existingTaiBan.get();
            taiBan.setLanTaiBan(taiBanDTO.getLanTaiBan());
            TaiBan updatedTaiBan = taiBanRepository.save(taiBan);
            return taiBanMapper.toDTO(updatedTaiBan);
        }
        return null;
    }
    
    public boolean deleteTaiBan(Integer id) {
        if (taiBanRepository.existsById(id)) {
            taiBanRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<TaiBanDTO> findByLanTaiBan(Integer lanTaiBan) {
        List<TaiBan> taiBans = taiBanRepository.findByLanTaiBan(lanTaiBan);
        return taiBanMapper.toDTOList(taiBans);
    }

}
