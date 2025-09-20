package com.example.app.repository;

import com.example.app.entity.TaiBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiBanRepository extends JpaRepository<TaiBan, Integer> {
    
    List<TaiBan> findByLanTaiBan(Integer lanTaiBan);
    
    List<TaiBan> findByNamTaiBan(Integer namTaiBan);
    
    List<TaiBan> findByLanTaiBanAndNamTaiBan(Integer lanTaiBan, Integer namTaiBan);
    
    boolean existsByLanTaiBanAndNamTaiBan(Integer lanTaiBan, Integer namTaiBan);
}
