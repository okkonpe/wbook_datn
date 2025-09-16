package com.example.app.repository;

import com.example.app.entity.LoaiBia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface LoaiBiaRepository extends JpaRepository<LoaiBia, Integer> {
        boolean existsByMaBia(String ma);
    }
