package com.example.app.repository;

import com.example.app.entity.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, Integer> {
    Optional<ChucVu> findByMaChucVu(String maChucVu);
    boolean existsByMaChucVu(String maChucVu);
} 