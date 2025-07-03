package com.example.app.repository;

import com.example.app.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia,Integer> {

    boolean existsByMaTacGia(String ma);
}
