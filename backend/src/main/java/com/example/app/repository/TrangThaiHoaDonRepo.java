package com.example.app.repository;

import com.example.app.entity.TrangThaiHoaDon;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TrangThaiHoaDonRepo extends JpaRepository<TrangThaiHoaDon,Integer> {
Optional<TrangThaiHoaDon> findByTrangThai(String trangThai);
}
