package com.example.app.repository;

import com.example.app.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByMaNv(String maNv);
    Optional<NhanVien> findByEmail(String email);
    Optional<NhanVien> findByCccd(String cccd);
    Optional<NhanVien> findByTaiKhoan(String taiKhoan);
    
    boolean existsByMaNv(String maNv);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    boolean existsByTaiKhoan(String taiKhoan);
    
    Page<NhanVien> findByTenNvContaining(String tenNv, Pageable pageable);
} 