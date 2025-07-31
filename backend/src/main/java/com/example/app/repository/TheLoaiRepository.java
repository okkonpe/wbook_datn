package com.example.app.repository;

import com.example.app.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {
    boolean existsByMaTheLoai(String ma);
}
