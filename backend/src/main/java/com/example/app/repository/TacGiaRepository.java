package com.example.app.repository;

import com.example.app.entity.NhanVien;
import com.example.app.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia,Integer> {

    boolean existsByMaTacGia(String ma);

    Set<TacGia> findByTenTacGia(String tenTG);

}
