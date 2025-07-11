package com.example.app.service;

import com.example.app.dto.nhanVienDTO.NhanVienDTO;
import com.example.app.entity.NhanVien;
import com.example.app.mapper.NhanVienMapper;
import com.example.app.repository.NhanVienRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    
    @Autowired
    private NhanVienMapper nhanVienMapper;

    public List<NhanVienDTO> getAllNhanVien() {
        return nhanVienRepository.findAll().stream()
                .map(nhanVienMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<NhanVienDTO> getAllNhanVienPaged(Pageable pageable) {
        return nhanVienRepository.findAll(pageable)
                .map(nhanVienMapper::toDTO);
    }
    
    public Page<NhanVienDTO> searchNhanVienByName(String name, Pageable pageable) {
        return nhanVienRepository.findByTenNvContaining(name, pageable)
                .map(nhanVienMapper::toDTO);
    }

    public NhanVienDTO getNhanVienById(Integer id) {
        return nhanVienRepository.findById(id)
                .map(nhanVienMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id = " + id));
    }

    public NhanVienDTO createNhanVien(NhanVienDTO nhanVienDTO) {
        // Tạo mã nhân viên tự động
        nhanVienDTO.setMaNv(generateUniqueMaNhanVien());
        
        // Kiểm tra thông tin trùng lặp
        validateUniqueConstraints(nhanVienDTO);

        NhanVien nhanVien = nhanVienMapper.toEntity(nhanVienDTO);
        NhanVien saved = nhanVienRepository.save(nhanVien);
        return nhanVienMapper.toDTO(saved);
    }

    public NhanVienDTO updateNhanVien(Integer id, NhanVienDTO nhanVienDTO) {
        NhanVien existingNhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id = " + id));

        // Kiểm tra thông tin trùng lặp, ngoại trừ mã nhân viên hiện tại
        validateUniqueConstraintsForUpdate(nhanVienDTO, existingNhanVien);
        
        // Cập nhật thông tin nhân viên, giữ nguyên mã nhân viên
        nhanVienDTO.setMaNv(existingNhanVien.getMaNv());
        NhanVien updated = nhanVienMapper.updateEntityFromDTO(nhanVienDTO, existingNhanVien);
        
        NhanVien saved = nhanVienRepository.save(updated);
        return nhanVienMapper.toDTO(saved);
    }

    public void deleteNhanVien(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id = " + id));
        
        nhanVienRepository.delete(nhanVien);
    }
    
    // Kiểm tra các ràng buộc duy nhất khi tạo mới
    private void validateUniqueConstraints(NhanVienDTO nhanVienDTO) {
        // Kiểm tra email
        if (nhanVienDTO.getEmail() != null && nhanVienRepository.existsByEmail(nhanVienDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        
        // Kiểm tra CCCD
        if (nhanVienDTO.getCccd() != null && nhanVienRepository.existsByCccd(nhanVienDTO.getCccd())) {
            throw new IllegalArgumentException("CCCD đã được sử dụng");
        }
        
        // Kiểm tra tài khoản
        if (nhanVienDTO.getTaiKhoan() != null && nhanVienRepository.existsByTaiKhoan(nhanVienDTO.getTaiKhoan())) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại");
        }
    }
    
    // Kiểm tra các ràng buộc duy nhất khi cập nhật
    private void validateUniqueConstraintsForUpdate(NhanVienDTO nhanVienDTO, NhanVien existingNhanVien) {
        // Kiểm tra email
        if (nhanVienDTO.getEmail() != null && !nhanVienDTO.getEmail().equals(existingNhanVien.getEmail())
                && nhanVienRepository.existsByEmail(nhanVienDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        
        // Kiểm tra CCCD
        if (nhanVienDTO.getCccd() != null && !nhanVienDTO.getCccd().equals(existingNhanVien.getCccd())
                && nhanVienRepository.existsByCccd(nhanVienDTO.getCccd())) {
            throw new IllegalArgumentException("CCCD đã được sử dụng");
        }
        
        // Kiểm tra tài khoản
        if (nhanVienDTO.getTaiKhoan() != null && !nhanVienDTO.getTaiKhoan().equals(existingNhanVien.getTaiKhoan())
                && nhanVienRepository.existsByTaiKhoan(nhanVienDTO.getTaiKhoan())) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại");
        }
    }

    // Tạo mã nhân viên duy nhất
    private String generateUniqueMaNhanVien() {
        String prefix = "NV";
        int number = 1;
        String ma;

        do {
            ma = prefix + String.format("%03d", number); // NV001, NV002,...
            number++;
        } while (nhanVienRepository.existsByMaNv(ma));

        return ma;
    }
} 