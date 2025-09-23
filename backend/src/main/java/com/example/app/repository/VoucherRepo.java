package com.example.app.repository;

import com.example.app.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher,Long> {
    Optional<Voucher> findByMaVoucher(String maVoucher);

}
