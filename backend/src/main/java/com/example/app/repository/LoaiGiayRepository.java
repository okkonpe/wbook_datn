package com.example.app.repository;

import com.example.app.entity.LoaiGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiGiayRepository extends JpaRepository<LoaiGiay, Integer> {
    boolean existsByMaGiay(String ma);
}
