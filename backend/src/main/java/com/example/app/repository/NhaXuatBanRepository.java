package com.example.app.repository;

import com.example.app.entity.NhaXuatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Integer> {

    boolean existsByMaNhaXuatBan(String ma);
}