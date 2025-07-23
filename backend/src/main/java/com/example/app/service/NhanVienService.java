package com.example.app.service;

import com.example.app.dto.nhanVienDTO.NhanVienDTO;
import com.example.app.entity.NhanVien;
import com.example.app.mapper.NhanVienMapper;
import com.example.app.repository.KhachHangRepo;
import com.example.app.repository.NhanVienRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepo khachHangRepo;
    
    @Autowired
    private NhanVienMapper nhanVienMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

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

    public NhanVienDTO createNhanVien(NhanVienDTO nhanVienDTO) throws JsonProcessingException {
        // Tạo mã nhân viên tự động
        nhanVienDTO.setMaNv(generateUniqueMaNhanVien());
        
        // Kiểm tra thông tin trùng lặp
        validateUniqueConstraints(nhanVienDTO);
        String rawPassword = generateRandomPassword(8);


        NhanVien nhanVien = nhanVienMapper.toEntity(nhanVienDTO);
        nhanVien.setMatKhau(passwordEncoder.encode(rawPassword));

        NhanVien saved = nhanVienRepository.save(nhanVien);
        emailService.sendPasswordToNhanVien(nhanVienDTO.getEmail(), nhanVienDTO.getTaiKhoan(), rawPassword);
        nhanVien.setMatKhau(null);
        return nhanVienMapper.toDTO(saved);
    }
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
    private void validateUniqueConstraints(NhanVienDTO nhanVienDTO) throws JsonProcessingException {
        // Kiểm tra email
        // 1. Kiểm tra Email
        Map<String, String> errors = new HashMap<>();

// 1. Email
        if (nhanVienDTO.getEmail() != null && nhanVienRepository.existsByEmail(nhanVienDTO.getEmail())) {
            errors.put("email", "Email đã được sử dụng");
        }

// 2. CCCD
        if (nhanVienDTO.getCccd() != null && nhanVienRepository.existsByCccd(nhanVienDTO.getCccd())) {
            errors.put("cccd", "CCCD đã được sử dụng");
        }

// 3. Tài khoản
        String taiKhoan = nhanVienDTO.getTaiKhoan();
        boolean existsNV = nhanVienRepository.existsByTaiKhoan(taiKhoan);
        boolean existsKH = khachHangRepo.existsByTaiKhoan(taiKhoan);

        if (taiKhoan != null && (existsNV || existsKH)) {
            errors.put("taiKhoan", "Tài khoản đã tồn tại");
        }

// ❗ Nếu có lỗi → ném custom exception chứa map lỗi
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(new ObjectMapper().writeValueAsString(errors));
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