package com.example.app.repository;

import com.example.app.entity.ChuDe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChuDeRepository extends JpaRepository<ChuDe, Integer> {
    boolean existsByMaChuDe(String ma);
}

