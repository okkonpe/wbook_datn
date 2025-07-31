package com.example.app.repository;

import com.example.app.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    boolean existsByMaKichThuoc(String ma);
}