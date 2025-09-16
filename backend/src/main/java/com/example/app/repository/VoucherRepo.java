package com.example.app.repository;

import com.example.app.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VoucherRepo extends JpaRepository<Voucher,Integer> {
    Optional<Voucher> findByMaVoucher(String maVoucher);
}
