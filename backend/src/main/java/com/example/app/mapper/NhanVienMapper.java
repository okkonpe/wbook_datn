package com.example.app.mapper;

import com.example.app.dto.nhanVienDTO.NhanVienDTO;
import com.example.app.entity.ChucVu;
import com.example.app.entity.NhanVien;
import com.example.app.repository.ChucVuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NhanVienMapper {

    @Autowired
    private ChucVuRepository chucVuRepository;

    public NhanVienDTO toDTO(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienDTO dto = new NhanVienDTO();
        dto.setId(entity.getId());
        dto.setMaNv(entity.getMaNv());
        dto.setTenNv(entity.getTenNv());
        dto.setLuong(entity.getLuong());
        dto.setSdt(entity.getSdt());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setEmail(entity.getEmail());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setCccd(entity.getCccd());
//        dto.setMatKhau(entity.getMatKhau());
        dto.setTaiKhoan(entity.getTaiKhoan());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setTrangThai(entity.getTrangThai());
        
//        if (entity.getChucVu() != null) {
//            dto.setChucVuId(entity.getChucVu().getId());
//        }
        
        return dto;
    }

    public NhanVien toEntity(NhanVienDTO dto) {
        if (dto == null) {
            return null;
        }

        NhanVien entity = new NhanVien();
        
        return updateEntityFromDTO(dto, entity);
    }
    
    public NhanVien updateEntityFromDTO(NhanVienDTO dto, NhanVien entity) {
        entity.setMaNv(dto.getMaNv());
        entity.setTenNv(dto.getTenNv());
        entity.setLuong(dto.getLuong());
        entity.setSdt(dto.getSdt());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setEmail(dto.getEmail());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setCccd(dto.getCccd());
//        entity.setMatKhau(dto.getMatKhau());
        entity.setTaiKhoan(dto.getTaiKhoan());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setTrangThai(dto.getTrangThai());
        
//        if (dto.getChucVuId() != null) {
//            ChucVu chucVu = chucVuRepository.findById(dto.getChucVuId())
//                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chức vụ với id = " + dto.getChucVuId()));
//            entity.setChucVu(chucVu);
//        }
        
        return entity;
    }
} 